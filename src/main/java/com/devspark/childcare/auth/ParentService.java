package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.ParentProfileResponseDto;
import com.devspark.childcare.child.ChildRepository;
import com.devspark.childcare.child.dto.ChildSummaryDto;
import com.devspark.childcare.child.dto.ChildResponseDto;
import com.devspark.childcare.child.Child;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    public ParentProfileResponseDto getParentProfile(String email) {
        Parent parent = parentRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent profile not found"));

        var children = childRepository.findByParentId(parent.getParentId()).stream()
                .map(c -> ChildSummaryDto.builder()
                        .childId(c.getChildId())
                        .name(c.getFirstName() + " " + c.getLastName())
                        .profilePic(c.getProfilePic())
                        .status(c.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        return ParentProfileResponseDto.builder()
                .parentId(parent.getParentId())
                .fullName(parent.getFullName())
                .email(parent.getAccount().getEmail())
                .profilePicture(parent.getProfilePicture())
                .role(parent.getAccount().getRole().name())
                .relationship(parent.getRelationship().name())
                .phone(parent.getPhone())
                .nic(parent.getNic())
                .address(parent.getAddress())
                .children(children)
                .build();
    }

    @Transactional
    public void updateParentProfile(String email, ParentProfileResponseDto dto) {
        Parent parent = parentRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent profile not found"));

        parent.setFullName(dto.getFullName());
        if (dto.getProfilePicture() != null) {
            parent.setProfilePicture(dto.getProfilePicture());
        }
        parent.setPhone(dto.getPhone());
        parent.setNic(dto.getNic());
        parent.setAddress(dto.getAddress());
        
        parentRepository.save(parent);
    }

    public com.devspark.childcare.child.dto.ChildResponseDto getChildProfile(String email, java.util.UUID childId) {
        Parent parent = parentRepository.findByAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent profile not found"));

        com.devspark.childcare.child.Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        // Security check
        if (!child.getParentId().equals(parent.getParentId())) {
            throw new RuntimeException("Unauthorized access to child profile");
        }

        return com.devspark.childcare.child.dto.ChildResponseDto.builder()
                .childId(child.getChildId())
                .firstName(child.getFirstName())
                .lastName(child.getLastName())
                .dob(child.getDob())
                .gender(child.getGender() != null ? child.getGender().name() : "OTHER")
                .bloodGroup(child.getBloodGroup())
                .height(child.getHeight() != null ? child.getHeight().doubleValue() : 0.0)
                .weight(child.getWeight() != null ? child.getWeight().doubleValue() : 0.0)
                .address(parent.getAddress()) // Get address from parent
                .specialNote(child.getSpecialNote())
                .profilePic(child.getProfilePic())
                .status(child.getStatus() != null ? child.getStatus().name() : "ENROLLED")
                .parentID(parent.getParentId().toString())
                .guardianName(parent.getFullName())
                .guardianEmail(parent.getAccount().getEmail())
                .build();
    }
}
