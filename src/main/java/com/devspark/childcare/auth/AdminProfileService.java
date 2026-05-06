package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.AdminProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProfileService {

    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;

    public AdminProfileDto getAdminProfile(String email) {
        Admin admin = adminRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));

        return AdminProfileDto.builder()
                .name(admin.getFullName())
                .email(admin.getAccount().getEmail())
                .role(admin.getAccount().getRole().name())
                .phone1(admin.getPhone1())
                .phone2(admin.getPhone2())
                .address(admin.getAddress())
                .centerName(admin.getCenterName())
                .capacity(String.valueOf(admin.getCapacity()))
                .profileImage(admin.getProfileImageUrl())
                .build();
    }

    @Transactional
    public void updateAdminProfile(String email, AdminProfileDto dto) {
        Admin admin = adminRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));

        admin.setFullName(dto.getName());
        admin.setPhone1(dto.getPhone1());
        admin.setPhone2(dto.getPhone2());
        admin.setAddress(dto.getAddress());
        admin.setCenterName(dto.getCenterName());
        if (dto.getCapacity() != null && !dto.getCapacity().isEmpty()) {
            admin.setCapacity(Integer.parseInt(dto.getCapacity()));
        }
        admin.setProfileImageUrl(dto.getProfileImage());

        adminRepository.save(admin);
    }
}
