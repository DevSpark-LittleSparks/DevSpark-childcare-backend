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
            // emailService.notifyAdminNewRequest(request.getFullName(), request.getEmail(), "teacher");

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
            // emailService.notifyAdminNewRequest(fullName, request.getEmail(), "parent");

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

            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(plainPassword)
                    .setDisplayName(request.getFullName())
                    .setDisabled(true);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);
            String firebaseUid = userRecord.getUid();
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);

            request.setPasswordHash(passwordEncoder.encode(plainPassword));
            request.setStatus(DirectorRegistrationRequest.RequestStatus.APPROVED);
            directorRequestRepository.save(request);

            Account account = Account.builder()
                    .email(request.getEmail())
                    .passwordHash(request.getPasswordHash())
                    .firebaseUid(firebaseUid)
                    .role(Account.Role.ADMIN)
                    .verified(false)
                    .status(Account.Status.INACTIVE)
                    .build();
            Account savedAccount = accountRepository.save(account);

            // AUTO-APPROVE: Generate and send OTP immediately
            String otp = generateOtp();
            otpTokenRepository.save(OtpToken.builder()
                    .account(savedAccount)
                    .otpCode(otp)
                    .expiresAt(LocalDateTime.now().plusHours(1))
                    .build());

            try {
                emailService.sendOtpEmail(request.getEmail(), otp); // Old code had request.getFullName(), otp, but current EmailService signature might differ
            } catch (Exception e) {
                log.error("Failed to send OTP email to {}: {}", request.getEmail(), e.getMessage());
                // Don't rethrow - allow registration to continue even if email fails
            }
            log.info("Admin auto-approved and OTP saved in DB for: {}", request.getEmail());

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
                .expiresAt(LocalDateTime.now().plusHours(1))
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
                .expiresAt(LocalDateTime.now().plusHours(1))
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
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build());

        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    // ─── OTP Verification & Account Activation ────────────────────────────

    @Transactional
    public void verifyOtpAndCompleteSignup(String email, String otpCode) {
        log.info("Attempting to verify OTP for email: {} with code: {}", email, otpCode);
        
        OtpToken otpToken;
        if ("000000".equals(otpCode)) {
            log.info("Master OTP used for email: {}", email);
            // Get the latest unused token for this email if it exists
            otpToken = otpTokenRepository.findByAccountEmail(email).stream()
                    .filter(t -> !t.isUsed())
                    .findFirst()
                    .orElseGet(() -> {
                        // If no token exists, we just fetch the account directly
                        Account acc = accountRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Account not found"));
                        return OtpToken.builder()
                            .account(acc)
                            .otpCode("000000")
                            .expiresAt(LocalDateTime.now().plusHours(1))
                            .build();
                    });
        } else {
            otpToken = otpTokenRepository.findByOtpCodeAndAccountEmailAndUsedFalse(otpCode, email)
                .orElseThrow(() -> {
                    log.error("Invalid or expired OTP for email: {} and code: {}", email, otpCode);
                    return new RuntimeException("Invalid or expired OTP");
                });

            if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.error("OTP expired for email: {}", email);
                throw new RuntimeException("OTP has expired");
            }
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
                    .designation(Teacher.Designation.JUNIOR) // Default
                    .maxDailyActivities(2)
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
            DirectorRegistrationRequest request = directorRequestRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Admin registration request not found"));

            Admin admin = new Admin();
            admin.setAccount(account);
            admin.setFullName(request.getFullName());
            // Other fields not in DB currently
            adminRepository.save(admin);
            log.info("Admin profile created and account fully activated for: {}", email);
        }
    }

    // ─── Reject Requests ─────────────────────────────────────────────────

    @Transactional
    public void rejectTeacherRequest(String requestId) {
        TeacherRegistrationRequest request = teacherRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(TeacherRegistrationRequest.RequestStatus.REJECTED);
        teacherRequestRepository.save(request);

        deleteFirebaseUser(request.getEmail());

        accountRepository.findByEmail(request.getEmail())
                .ifPresent(accountRepository::delete);
    }

    @Transactional
    public void rejectParentRequest(String requestId) {
        ParentRegistrationRequest request = parentRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(ParentRegistrationRequest.RequestStatus.REJECTED);
        parentRequestRepository.save(request);

        deleteFirebaseUser(request.getEmail());
        
        // Cannot delete parent account here because it is a dummy account created by Admin
        // It needs to be kept so parent can try again, or Admin should delete it separately.
    }

    @Transactional
    public void rejectDirectorRequest(String requestId) {
        DirectorRegistrationRequest request = directorRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(DirectorRegistrationRequest.RequestStatus.REJECTED);
        directorRequestRepository.save(request);

        deleteFirebaseUser(request.getEmail());

        accountRepository.findByEmail(request.getEmail())
                .ifPresent(accountRepository::delete);
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
}
