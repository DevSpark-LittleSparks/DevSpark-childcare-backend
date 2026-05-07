package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.TeacherProfileDto;
import com.devspark.childcare.staff.Teacher;
import com.devspark.childcare.staff.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherProfileService {

    private final TeacherRepository teacherRepository;

    public TeacherProfileDto getTeacherProfile(String email) {
        Teacher teacher = teacherRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        return TeacherProfileDto.builder()
                .name(teacher.getFullName())
                .email(teacher.getAccount().getEmail())
                .role(teacher.getAccount().getRole().name())
                .phone(teacher.getPhone())
                .address(teacher.getAddress())
                .bio(teacher.getBio())
                .designation(teacher.getDesignation().name())
                .profileImage(teacher.getProfilePicture())
                .build();
    }

    @Transactional
    public void updateTeacherProfile(String email, TeacherProfileDto dto) {
        Teacher teacher = teacherRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        teacher.setFullName(dto.getName());
        teacher.setPhone(dto.getPhone());
        teacher.setAddress(dto.getAddress());
        teacher.setBio(dto.getBio());
        teacher.setProfilePicture(dto.getProfileImage());

        teacherRepository.save(teacher);
    }
}
