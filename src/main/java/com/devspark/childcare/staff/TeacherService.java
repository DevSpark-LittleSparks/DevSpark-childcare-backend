package com.devspark.childcare.staff;

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
}
