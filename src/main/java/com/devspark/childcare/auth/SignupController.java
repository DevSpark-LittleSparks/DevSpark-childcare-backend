package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.OtpVerificationDto;
import com.devspark.childcare.auth.dto.ParentSignupRequestDto;
import com.devspark.childcare.auth.dto.TeacherSignupRequestDto;
import com.devspark.childcare.staff.TeacherRegistrationRequest;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/signup")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/teacher/request")
    public ApiResponse<String> submitTeacherRequest(@RequestBody TeacherSignupRequestDto dto) {
        TeacherRegistrationRequest request = TeacherRegistrationRequest.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .designation(TeacherRegistrationRequest.Designation.valueOf(dto.getDesignation()))
                .build();
        
        signupService.submitTeacherRequest(request, dto.getPassword());
        return ApiResponse.success("Teacher signup request submitted. Awaiting admin approval.", null);
    }

    @PostMapping("/parent/request")
    public ApiResponse<String> submitParentRequest(@RequestBody ParentSignupRequestDto dto) {
        ParentRegistrationRequest request = ParentRegistrationRequest.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nic(dto.getNic())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .relationship(ParentRegistrationRequest.Relationship.valueOf(dto.getRelationship()))
                .build();

        signupService.submitParentRequest(request, dto.getPassword());
        return ApiResponse.success("Parent signup request submitted. Awaiting admin approval.", null);
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@RequestBody OtpVerificationDto dto) {
        signupService.verifyOtpAndCompleteSignup(dto.getEmail(), dto.getOtp());
        return ApiResponse.success("Signup successful! You can now login.", null);
    }
}
