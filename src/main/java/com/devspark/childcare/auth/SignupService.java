package com.devspark.childcare.auth;

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
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final TeacherRegistrationRequestRepository teacherRequestRepository;
    private final ParentRegistrationRequestRepository parentRequestRepository;
    private final AccountRepository accountRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void submitTeacherRequest(TeacherRegistrationRequest request, String plainPassword) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        try {
            // 1. Create User in Firebase
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", Account.Role.TEACHER.name());

            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(plainPassword)
                    .setDisplayName(request.getFullName())
                    .setDisabled(true); // Disable until approved and OTP verified

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);
            String firebaseUid = userRecord.getUid();

            // Set custom claims (roles)
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);

            // 2. Save Request with Hashed Password (as fallback/local reference)
            request.setPasswordHash(passwordEncoder.encode(plainPassword));
            teacherRequestRepository.save(request);

            // 3. Create Local Account Link
            Account account = Account.builder()
                    .email(request.getEmail())
                    .passwordHash(request.getPasswordHash())
                    .firebaseUid(firebaseUid)
                    .role(Account.Role.TEACHER)
                    .verified(false)
                    .status(Account.Status.INACTIVE)
                    .build();
            accountRepository.save(account);

        } catch (Exception e) {
            log.error("Error creating Firebase user: ", e);
            throw new RuntimeException("Failed to initiate signup: " + e.getMessage());
        }
    }

    @Transactional
    public void submitParentRequest(ParentRegistrationRequest request, String plainPassword) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
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

            // Set custom claims (roles)
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);

            request.setPasswordHash(passwordEncoder.encode(plainPassword));
            parentRequestRepository.save(request);

            Account account = Account.builder()
                    .email(request.getEmail())
                    .passwordHash(request.getPasswordHash())
                    .firebaseUid(firebaseUid)
                    .role(Account.Role.PARENT)
                    .verified(false)
                    .status(Account.Status.INACTIVE)
                    .build();
            accountRepository.save(account);

        } catch (Exception e) {
            log.error("Error creating Firebase user: ", e);
            throw new RuntimeException("Failed to initiate signup: " + e.getMessage());
        }
    }

    @Transactional
    public void approveTeacherRequest(String requestId) {
        TeacherRegistrationRequest request = teacherRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(TeacherRegistrationRequest.RequestStatus.APPROVED);
        teacherRequestRepository.save(request);

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Generate and Send OTP
        String otp = generateOtp();
        OtpToken otpToken = OtpToken.builder()
                .account(account)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        otpTokenRepository.save(otpToken);

        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    @Transactional
    public void approveParentRequest(String requestId) {
        ParentRegistrationRequest request = parentRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(ParentRegistrationRequest.RequestStatus.APPROVED);
        parentRequestRepository.save(request);

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String otp = generateOtp();
        OtpToken otpToken = OtpToken.builder()
                .account(account)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        otpTokenRepository.save(otpToken);

        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    @Transactional
    public void verifyOtpAndCompleteSignup(String email, String otpCode) {
        OtpToken otpToken = otpTokenRepository.findByOtpCodeAndAccountEmailAndUsedFalse(otpCode, email)
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        Account account = otpToken.getAccount();
        account.setVerified(true);
        account.setStatus(Account.Status.ACTIVE);
        accountRepository.save(account);

        try {
            // Enable user in Firebase
            UserRecord.UpdateRequest firebaseUpdate = new UserRecord.UpdateRequest(account.getFirebaseUid())
                    .setDisabled(false);
            FirebaseAuth.getInstance().updateUser(firebaseUpdate);
        } catch (Exception e) {
            log.error("Error enabling Firebase user: ", e);
            throw new RuntimeException("Failed to enable user in Firebase");
        }

        if (account.getRole() == Account.Role.TEACHER) {
            TeacherRegistrationRequest request = teacherRequestRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            
            Teacher teacher = Teacher.builder()
                    .account(account)
                    .fullName(request.getFullName())
                    .designation(Teacher.Designation.valueOf(request.getDesignation().name()))
                    .maxDailyActivities(request.getDesignation() == TeacherRegistrationRequest.Designation.SENIOR ? 5 : 2)
                    .build();
            teacherRepository.save(teacher);
        } else if (account.getRole() == Account.Role.PARENT) {
            ParentRegistrationRequest request = parentRequestRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Request not found"));

            Parent parent = Parent.builder()
                    .account(account)
                    .fullName(request.getFirstName() + " " + request.getLastName())
                    .address(request.getAddress())
                    .phone(request.getPhone())
                    .nic(request.getNic())
                    .relationship(Parent.Relationship.valueOf(request.getRelationship().name()))
                    .build();
            parentRepository.save(parent);
        }
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
