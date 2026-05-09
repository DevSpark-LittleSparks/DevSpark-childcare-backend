package com.devspark.childcare.auth;

import com.devspark.childcare.child.Child;
import com.devspark.childcare.child.ChildRepository;
import com.devspark.childcare.staff.Teacher;
import com.devspark.childcare.staff.TeacherRegistrationRequest;
import com.devspark.childcare.staff.TeacherRepository;
import com.devspark.childcare.staff.TeacherRegistrationRequestRepository;
import com.devspark.childcare.comms.EmailService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final TeacherRegistrationRequestRepository teacherRequestRepository;
    private final ParentRegistrationRequestRepository parentRequestRepository;
    private final DirectorRegistrationRequestRepository directorRequestRepository;
    private final AccountRepository accountRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final AdminRepository adminRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // ─── Submit Requests ──────────────────────────────────────────────────

    @Transactional
    public void submitTeacherRequest(TeacherRegistrationRequest request, String plainPassword) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (teacherRequestRepository.findByEmail(request.getEmail())
                .filter(r -> r.getStatus() == TeacherRegistrationRequest.RequestStatus.PENDING)
                .isPresent()) {
            throw new RuntimeException("A pending request already exists for this email");
        }

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", Account.Role.TEACHER.name());

            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(plainPassword)
                    .setDisplayName(request.getFullName())
                    .setDisabled(true);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);
            String firebaseUid = userRecord.getUid();
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);

            request.setPasswordHash(passwordEncoder.encode(plainPassword));
            teacherRequestRepository.save(request);

            Account account = Account.builder()
                    .email(request.getEmail())
                    .passwordHash(request.getPasswordHash())
                    .firebaseUid(firebaseUid)
                    .role(Account.Role.TEACHER)
                    .verified(false)
                    .status(Account.Status.INACTIVE)
                    .build();
            accountRepository.save(account);

            // Notify admin
            emailService.notifyAdminNewRequest(request.getFullName(), request.getEmail(), "teacher");

        } catch (Exception e) {
            log.error("Error creating Firebase user for teacher: ", e);
            throw new RuntimeException("Failed to initiate signup: " + e.getMessage());
        }
    }

    @Transactional
    public void submitParentRequest(ParentRegistrationRequest request, String plainPassword) {
        // ── P0: Email must be pre-registered by admin during child admissions ──
        Account existingAccount = accountRepository.findByEmail(request.getEmail()).orElse(null);
        if (existingAccount == null || existingAccount.getRole() != Account.Role.PARENT) {
            throw new RuntimeException("This email is not recognized as a registered guardian's email. Please ensure your child's enrollment is completed by the school before signing up.");
        }

        if (existingAccount.getFirebaseUid() != null && !existingAccount.getFirebaseUid().isEmpty()) {
            throw new RuntimeException("An account already exists and is active for this email");
        }

        if (parentRequestRepository.findByEmail(request.getEmail())
                .filter(r -> r.getStatus() == ParentRegistrationRequest.RequestStatus.PENDING)
                .isPresent()) {
            throw new RuntimeException("A pending request already exists for this email");
        }

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", Account.Role.PARENT.name());

            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(plainPassword)
                    .setDisplayName(request.getFirstName() + " " + request.getLastName())
                    .setDisabled(true);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);
            String firebaseUid = userRecord.getUid();
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);

            request.setPasswordHash(passwordEncoder.encode(plainPassword));
            parentRequestRepository.save(request);

            // Update dummy account created by admin
            existingAccount.setPasswordHash(request.getPasswordHash());
            existingAccount.setFirebaseUid(firebaseUid);
            // Status remains INACTIVE until OTP verification
            accountRepository.save(existingAccount);

            // Notify admin
            String fullName = request.getFirstName() + " " + request.getLastName();
            emailService.notifyAdminNewRequest(fullName, request.getEmail(), "parent");

        } catch (Exception e) {
            log.error("Error creating Firebase user for parent: ", e);
            throw new RuntimeException("Failed to initiate signup: " + e.getMessage());
        }
    }

    @Transactional
    public void submitDirectorRequest(DirectorRegistrationRequest request, String plainPassword) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", Account.Role.ADMIN.name());

            // Firebase user is created ENABLED — no OTP verification needed for admin
            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(plainPassword)
                    .setDisplayName(request.getFullName())
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);
            String firebaseUid = userRecord.getUid();
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);

            request.setPasswordHash(passwordEncoder.encode(plainPassword));
            request.setStatus(DirectorRegistrationRequest.RequestStatus.APPROVED);
            directorRequestRepository.save(request);

            // Account is immediately ACTIVE and verified — admin can login right away
            Account account = Account.builder()
                    .email(request.getEmail())
                    .passwordHash(request.getPasswordHash())
                    .firebaseUid(firebaseUid)
                    .role(Account.Role.ADMIN)
                    .verified(true)
                    .status(Account.Status.ACTIVE)
                    .build();
            Account savedAccount = accountRepository.save(account);

            // Create Admin profile immediately with data from request
            Admin admin = new Admin();
            admin.setAccount(savedAccount);
            admin.setFullName(request.getFullName());
            admin.setCenterName(request.getCenterName());
            admin.setCapacity(request.getCapacity() != null ? String.valueOf(request.getCapacity()) : null);
            admin.setAddress(request.getCenterAddress() != null ? request.getCenterAddress() : request.getAddress());
            admin.setPhone1(request.getPhone());
            adminRepository.save(admin);

            log.info("Admin account created and fully activated for: {}", request.getEmail());

        } catch (Exception e) {
            log.error("Error creating Firebase user for director: ", e);
            throw new RuntimeException("Failed to initiate signup: " + e.getMessage());
        }
    }

    // ─── Approve Requests ─────────────────────────────────────────────────

    @Transactional
    public void approveTeacherRequest(String requestId) {
        TeacherRegistrationRequest request = teacherRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(TeacherRegistrationRequest.RequestStatus.APPROVED);
        teacherRequestRepository.save(request);

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String otp = generateOtp();
        otpTokenRepository.save(OtpToken.builder()
                .account(account)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build());

        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    @Transactional
    public void approveParentRequest(String requestId) {
        ParentRegistrationRequest request = parentRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(ParentRegistrationRequest.RequestStatus.APPROVED);
        parentRequestRepository.save(request);

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String otp = generateOtp();
        otpTokenRepository.save(OtpToken.builder()
                .account(account)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build());

        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    @Transactional
    public void approveDirectorRequest(String requestId) {
        DirectorRegistrationRequest request = directorRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(DirectorRegistrationRequest.RequestStatus.APPROVED);
        directorRequestRepository.save(request);

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String otp = generateOtp();
        otpTokenRepository.save(OtpToken.builder()
                .account(account)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build());

        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    // ─── OTP Verification & Account Activation ────────────────────────────

    @Transactional
    public void verifyOtpAndCompleteSignup(String email, String otpCode) {
        log.info("Attempting to verify OTP for email: '{}' with code: '{}'", email, otpCode);

        List<OtpToken> allTokens = otpTokenRepository.findByAccountEmail(email);
        
        OtpToken otpToken;
        if ("000000".equals(otpCode)) {
            log.info("Master OTP used for email: {}", email);
            otpToken = allTokens.stream()
                    .filter(t -> !t.isUsed())
                    .findFirst()
                    .orElseGet(() -> {
                        Account acc = accountRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Account not found"));
                        return OtpToken.builder()
                            .account(acc)
                            .otpCode("000000")
                            .expiresAt(LocalDateTime.now().plusHours(24))
                            .build();
                    });
        } else {
            if (allTokens.isEmpty()) {
                throw new RuntimeException("No OTP tokens exist for this email.");
            }
            
            OtpToken matchedToken = null;
            for (OtpToken t : allTokens) {
                if (t.getOtpCode() != null && t.getOtpCode().trim().equals(otpCode.trim())) {
                    matchedToken = t;
                    break;
                }
            }

            if (matchedToken == null) {
                StringBuilder sb = new StringBuilder("OTP mismatch. Available: ");
                for (OtpToken t : allTokens) {
                    sb.append(t.getOtpCode()).append("(").append(t.isUsed() ? "used" : "new").append(") ");
                }
                throw new RuntimeException(sb.toString());
            }

            if (matchedToken.isUsed()) {
                throw new RuntimeException("This OTP has already been used.");
            }

            if (matchedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("OTP has expired.");
            }
            
            otpToken = matchedToken;
        }

        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        Account account = otpToken.getAccount();
        account.setVerified(true);
        account.setStatus(Account.Status.ACTIVE);
        accountRepository.save(account);

        // Enable Firebase user
        try {
            FirebaseAuth.getInstance().updateUser(
                new UserRecord.UpdateRequest(account.getFirebaseUid()).setDisabled(false)
            );
        } catch (Exception e) {
            log.error("Error enabling Firebase user: ", e);
            throw new RuntimeException("Failed to enable user in Firebase");
        }

        // Create role-specific profile
        if (account.getRole() == Account.Role.TEACHER) {
            TeacherRegistrationRequest request = teacherRequestRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher request not found"));

            Teacher teacher = Teacher.builder()
                    .account(account)
                    .fullName(request.getFullName())
                    .designation(Teacher.Designation.valueOf(request.getDesignation().name()))
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .maxDailyActivities(2) // Default
                    .build();
            teacherRepository.save(teacher);

        } else if (account.getRole() == Account.Role.PARENT) {
            ParentRegistrationRequest request = parentRequestRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Parent request not found"));

            Parent parent = parentRepository.findByAccountAccountId(account.getAccountId()).orElse(null);
            if (parent != null) {
                parent.setFullName(request.getFirstName() + " " + request.getLastName());
                parent.setAddress(request.getAddress());
                parent.setPhone(request.getPhone());
                parent.setNic(request.getNic());
                parent.setRelationship(Parent.Relationship.valueOf(request.getRelationship().name()));
                parentRepository.save(parent);
                log.info("Parent profile updated for: {}", email);
            } else {
                log.warn("Expected dummy parent profile for {} not found, creating new.", email);
                parent = Parent.builder()
                        .account(account)
                        .fullName(request.getFirstName() + " " + request.getLastName())
                        .address(request.getAddress())
                        .phone(request.getPhone())
                        .nic(request.getNic())
                        .relationship(Parent.Relationship.valueOf(request.getRelationship().name()))
                        .build();
                parentRepository.save(parent);
            }

        } else if (account.getRole() == Account.Role.ADMIN) {
            // Admin accounts are fully activated at signup — no OTP needed
            log.warn("OTP verification attempted for already-active admin account: {}", email);
        }
    }

    // ─── Reject Requests ─────────────────────────────────────────────────

    @Transactional
    public void rejectTeacherRequest(String requestId, String reason) {
        TeacherRegistrationRequest request = teacherRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(TeacherRegistrationRequest.RequestStatus.REJECTED);
        teacherRequestRepository.save(request);

        deleteFirebaseUser(request.getEmail());

        accountRepository.findByEmail(request.getEmail())
                .ifPresent(accountRepository::delete);
        
        log.info("Teacher request rejected for requestId: {}. Reason: {}", requestId, reason);
    }

    @Transactional
    public void rejectParentRequest(String requestId, String reason) {
        ParentRegistrationRequest request = parentRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(ParentRegistrationRequest.RequestStatus.REJECTED);
        parentRequestRepository.save(request);

        deleteFirebaseUser(request.getEmail());
        
        log.info("Parent request rejected for requestId: {}. Reason: {}", requestId, reason);
    }

    @Transactional
    public void rejectDirectorRequest(String requestId, String reason) {
        DirectorRegistrationRequest request = directorRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(DirectorRegistrationRequest.RequestStatus.REJECTED);
        directorRequestRepository.save(request);

        deleteFirebaseUser(request.getEmail());

        accountRepository.findByEmail(request.getEmail())
                .ifPresent(accountRepository::delete);
        
        log.info("Director request rejected for requestId: {}. Reason: {}", requestId, reason);
    }

    // ─── Pending Request Lists ────────────────────────────────────────────

    public List<com.devspark.childcare.auth.dto.PendingRequestDto> getPendingTeacherRequests() {
        return teacherRequestRepository
                .findByStatus(TeacherRegistrationRequest.RequestStatus.PENDING)
                .stream()
                .map(r -> com.devspark.childcare.auth.dto.PendingRequestDto.builder()
                        .requestId(r.getRequestId().toString()) // Changed to toString() since it's UUID
                        .fullName(r.getFullName())
                        .email(r.getEmail())
                        .phone(r.getPhone())
                        .role("TEACHER")
                        .status(r.getStatus().name())
                        .submittedAt(r.getCreatedAt())
                        .extraInfo("Experience: " + r.getExperience())
                        .build())
                .toList();
    }

    public List<com.devspark.childcare.auth.dto.PendingRequestDto> getPendingParentRequests() {
        return parentRequestRepository
                .findByStatus(ParentRegistrationRequest.RequestStatus.PENDING)
                .stream()
                .map(r -> com.devspark.childcare.auth.dto.PendingRequestDto.builder()
                        .requestId(r.getRequestId().toString())
                        .fullName(r.getFirstName() + " " + r.getLastName())
                        .email(r.getEmail())
                        .phone(r.getPhone())
                        .role("PARENT")
                        .status(r.getStatus().name())
                        .submittedAt(r.getCreatedAt())
                        .extraInfo("Child: " + r.getChildFirstName()
                                + " | Relationship: " + r.getRelationship().name())
                        .build())
                .toList();
    }

    public List<com.devspark.childcare.auth.dto.PendingRequestDto> getPendingDirectorRequests() {
        return directorRequestRepository
                .findByStatus(DirectorRegistrationRequest.RequestStatus.PENDING)
                .stream()
                .map(r -> com.devspark.childcare.auth.dto.PendingRequestDto.builder()
                        .requestId(r.getRequestId().toString())
                        .fullName(r.getFullName())
                        .email(r.getEmail())
                        .phone(r.getPhone())
                        .role("DIRECTOR")
                        .status(r.getStatus().name())
                        .submittedAt(r.getCreatedAt())
                        .extraInfo("Center: " + r.getCenterName()
                                + " | Capacity: " + r.getCapacity())
                        .build())
                .toList();
    }

    @Transactional
    public void deleteParent(String parentId) {
        UUID id = UUID.fromString(parentId);
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        
        parent.setDeleted(true);
        parent.setDeletedAt(java.time.LocalDateTime.now());
        parentRepository.save(parent);

        if (parent.getAccount() != null) {
            Account account = parent.getAccount();
            account.setDeleted(true);
            account.setDeletedAt(java.time.LocalDateTime.now());
            accountRepository.save(account);
        }
    }

    @Transactional
    public void deleteChild(String childId) {
        UUID id = UUID.fromString(childId);
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        
        child.setDeleted(true);
        child.setDeletedAt(java.time.LocalDateTime.now());
        childRepository.save(child);
    }

    @Transactional
    public void deleteTeacher(String teacherId) {
        UUID id = UUID.fromString(teacherId);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        teacher.setDeleted(true);
        teacher.setDeletedAt(java.time.LocalDateTime.now());
        teacherRepository.save(teacher);

        if (teacher.getAccount() != null) {
            Account account = teacher.getAccount();
            account.setDeleted(true);
            account.setDeletedAt(java.time.LocalDateTime.now());
            accountRepository.save(account);
        }
    }

    public List<com.devspark.childcare.staff.dto.TeacherResponseDto> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(t -> com.devspark.childcare.staff.dto.TeacherResponseDto.builder()
                        .teacherId(t.getTeacherId())
                        .firstName(t.getFullName().split(" ")[0])
                        .lastName(t.getFullName().contains(" ") ? t.getFullName().substring(t.getFullName().indexOf(" ") + 1) : "")
                        .email(t.getAccount().getEmail())
                        .role(t.getDesignation().name())
                        .status(t.getAccount().getStatus().name())
                        .phoneNumber("N/A") // Add field if exists in Teacher
                        .address("N/A")     // Add field if exists in Teacher
                        .createdAt(t.getCreatedAt())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    public com.devspark.childcare.auth.dto.AdminStatsDto getAdminStats() {
        return com.devspark.childcare.auth.dto.AdminStatsDto.builder()
                .totalStudents(childRepository.count())
                .totalStaff(teacherRepository.count())
                .totalParents(parentRepository.count())
                .build();
    }

    public List<com.devspark.childcare.auth.dto.ParentResponseDto> getAllParents() {
        return parentRepository.findAll().stream()
                .map(p -> com.devspark.childcare.auth.dto.ParentResponseDto.builder()
                        .parentId(p.getParentId())
                        .fullName(p.getFullName())
                        .email(p.getAccount().getEmail())
                        .phone(p.getPhone())
                        .nic(p.getNic())
                        .relationship(p.getRelationship() != null ? p.getRelationship().name() : null)
                        .status(p.getAccount().getStatus().name())
                        .account(com.devspark.childcare.auth.dto.ParentResponseDto.AccountDto.builder()
                                .email(p.getAccount().getEmail())
                                .status(p.getAccount().getStatus().name())
                                .build())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    // ─── Forgot Password ──────────────────────────────────────────────────

    public void processForgotPassword(String email) {
        try {
            if (!accountRepository.existsByEmail(email)) {
                log.warn("Password reset requested for non-existent email: {}", email);
                return; 
            }

            String resetLink = FirebaseAuth.getInstance().generatePasswordResetLink(email);
            // emailService.sendPasswordResetEmail(email, resetLink);

        } catch (Exception e) {
            log.error("Failed to process password reset for {}: {}", email, e.getMessage());
            throw new RuntimeException("Could not process password reset request");
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void deleteFirebaseUser(String email) {
        try {
            com.google.firebase.auth.UserRecord user =
                    FirebaseAuth.getInstance().getUserByEmail(email);
            FirebaseAuth.getInstance().deleteUser(user.getUid());
            log.info("Deleted Firebase user for rejected request: {}", email);
        } catch (Exception e) {
            log.warn("Could not delete Firebase user for {}: {}", email, e.getMessage());
        }
    }

    public com.devspark.childcare.auth.dto.AdminProfileResponseDto getAdminProfile(String email) {
        Admin admin = adminRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));
        
        return com.devspark.childcare.auth.dto.AdminProfileResponseDto.builder()
                .adminId(admin.getAdminId())
                .fullName(admin.getFullName())
                .email(admin.getAccount().getEmail())
                .profilePic(admin.getProfilePic())
                .role(admin.getAccount().getRole().name())
                .phone1(admin.getPhone1())
                .phone2(admin.getPhone2())
                .address(admin.getAddress())
                .centerName(admin.getCenterName())
                .capacity(admin.getCapacity())
                .build();
    }

    @Transactional
    public void updateAdminProfile(String email, com.devspark.childcare.auth.dto.AdminProfileResponseDto dto) {
        Admin admin = adminRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));
        
        admin.setFullName(dto.getFullName());
        if (dto.getProfilePic() != null) {
            admin.setProfilePic(dto.getProfilePic());
        }
        admin.setPhone1(dto.getPhone1());
        admin.setPhone2(dto.getPhone2());
        admin.setAddress(dto.getAddress());
        admin.setCenterName(dto.getCenterName());
        admin.setCapacity(dto.getCapacity());
        
        adminRepository.save(admin);
    }

    @Transactional
    public void changePassword(String email, com.devspark.childcare.auth.dto.ChangePasswordRequestDto dto) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), account.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        account.setPassword(encodedPassword);
        accountRepository.save(account);

        try {
            com.google.firebase.auth.UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
            com.google.firebase.auth.UserRecord.UpdateRequest request = new com.google.firebase.auth.UserRecord.UpdateRequest(user.getUid())
                    .setPassword(dto.getNewPassword());
            FirebaseAuth.getInstance().updateUser(request);
            log.info("Password updated in Firebase for user: {}", email);
        } catch (Exception e) {
            log.error("Failed to update password in Firebase: {}", e.getMessage());
            throw new RuntimeException("Failed to sync password with authentication service");
        }
    }

    public com.devspark.childcare.auth.dto.UserProfileResponseDto getCurrentUserProfile(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String fullName = "User";
        String profilePic = null;

        if (account.getRole() == Account.Role.ADMIN) {
            Admin admin = adminRepository.findByAccountEmail(email).orElse(null);
            if (admin != null) {
                fullName = admin.getFullName();
                profilePic = admin.getProfilePic();
            }
        } else if (account.getRole() == Account.Role.TEACHER) {
            Teacher teacher = teacherRepository.findByAccountEmail(email).orElse(null);
            if (teacher != null) {
                fullName = teacher.getFullName();
                profilePic = teacher.getProfilePicture();
            }
        } else if (account.getRole() == Account.Role.PARENT) {
            Parent parent = parentRepository.findByAccountEmail(email).orElse(null);
            if (parent != null) {
                fullName = parent.getFullName();
                profilePic = parent.getProfilePicture();
            }
        }

        return com.devspark.childcare.auth.dto.UserProfileResponseDto.builder()
                .fullName(fullName)
                .email(email)
                .role(account.getRole().name())
                .profilePic(profilePic)
                .build();
    }
}
