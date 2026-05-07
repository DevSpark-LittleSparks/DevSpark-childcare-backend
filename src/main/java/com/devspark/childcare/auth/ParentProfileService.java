package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.ParentProfileDto;
import com.devspark.childcare.child.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentProfileService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    public ParentProfileDto getParentProfile(String email) {
        Parent parent = parentRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent profile not found"));

        return ParentProfileDto.builder()
                .name(parent.getFullName())
                .email(parent.getAccount().getEmail())
                .role(parent.getAccount().getRole().name())
                .phone1(parent.getPhone())
                .phone2(parent.getPhone2())
                .address(parent.getAddress())
                .relationship(parent.getRelationship() != null ? parent.getRelationship().name() : "GUARDIAN")
                .profileImage(parent.getProfilePicture())
                .children(childRepository.findByParentId(parent.getParentId()).stream()
                        .map(child -> ParentProfileDto.ChildSummaryDto.builder()
                                .id(child.getChildId())
                                .name(child.getFirstName() + " " + child.getLastName())
                                .profileImage(child.getProfilePic())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void updateParentProfile(String email, ParentProfileDto dto) {
        Parent parent = parentRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent profile not found"));

        parent.setFullName(dto.getName());
        parent.setPhone(dto.getPhone1());
        parent.setPhone2(dto.getPhone2());
        parent.setAddress(dto.getAddress());
        parent.setProfilePicture(dto.getProfileImage());

        parentRepository.save(parent);
    }
}
