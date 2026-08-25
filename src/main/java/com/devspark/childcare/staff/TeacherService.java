package com.devspark.childcare.staff;

import com.devspark.childcare.auth.AccountRepository;
import com.devspark.childcare.staff.dto.TeacherProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final AccountRepository accountRepository;

    // --- ADDED NEW METHOD FOR ADMIN TO GET ALL TEACHERS ---
    @Transactional(readOnly = true) // Architecture rule: Read-only transactions for fetch operations[cite: 3]
    public Page<TeacherProfileResponseDto> getAllTeachers(Pageable pageable) {
        // Architecture rule: Never expose Entities directly, always map to DTOs[cite: 3]
        return teacherRepository.findAll(pageable)
                .map(teacher -> TeacherProfileResponseDto.builder()
                        .teacherId(teacher.getTeacherId())
                        .fullName(teacher.getFullName())
                        .email(teacher.getAccount() != null ? teacher.getAccount().getEmail() : null)
                        .profilePicture(teacher.getProfilePicture())
                        .role(teacher.getAccount() != null && teacher.getAccount().getRole() != null ? teacher.getAccount().getRole().name() : null)
                        .designation(teacher.getDesignation() != null ? teacher.getDesignation().name() : null)
                        .phone(teacher.getPhone())
                        .address(teacher.getAddress())
                        .maxDailyActivities(teacher.getMaxDailyActivities())
                        .build()
                );
    }

    public TeacherProfileResponseDto getTeacherProfile(String email) {
        Teacher teacher = teacherRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        return TeacherProfileResponseDto.builder()
                .teacherId(teacher.getTeacherId())
                .fullName(teacher.getFullName())
                .email(teacher.getAccount().getEmail())
                .profilePicture(teacher.getProfilePicture())
                .role(teacher.getAccount().getRole().name())
                .designation(teacher.getDesignation().name())
                .phone(teacher.getPhone())
                .address(teacher.getAddress())
                .maxDailyActivities(teacher.getMaxDailyActivities())
                .build();
    }

    @Transactional
    public void updateTeacherProfile(String email, TeacherProfileResponseDto dto) {
        Teacher teacher = teacherRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        teacher.setFullName(dto.getFullName());
        if (dto.getProfilePicture() != null) {
            teacher.setProfilePicture(dto.getProfilePicture());
        }
        teacher.setPhone(dto.getPhone());
        teacher.setAddress(dto.getAddress());

        teacherRepository.save(teacher);
    }
}
/*package com.devspark.childcare.staff;

import com.devspark.childcare.auth.Account;
import com.devspark.childcare.auth.AccountRepository;
import com.devspark.childcare.staff.dto.TeacherProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final AccountRepository accountRepository;

    public TeacherProfileResponseDto getTeacherProfile(String email) {
        Teacher teacher = teacherRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        return TeacherProfileResponseDto.builder()
                .teacherId(teacher.getTeacherId())
                .fullName(teacher.getFullName())
                .email(teacher.getAccount().getEmail())
                .profilePicture(teacher.getProfilePicture())
                .role(teacher.getAccount().getRole().name())
                .designation(teacher.getDesignation().name())
                .phone(teacher.getPhone())
                .address(teacher.getAddress())
                .maxDailyActivities(teacher.getMaxDailyActivities())
                .build();
    }

    @Transactional
    public void updateTeacherProfile(String email, TeacherProfileResponseDto dto) {
        Teacher teacher = teacherRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        teacher.setFullName(dto.getFullName());
        if (dto.getProfilePicture() != null) {
            teacher.setProfilePicture(dto.getProfilePicture());
        }
        teacher.setPhone(dto.getPhone());
        teacher.setAddress(dto.getAddress());
        
        teacherRepository.save(teacher);
    }
}*/
