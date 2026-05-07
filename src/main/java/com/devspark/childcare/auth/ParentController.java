package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.ParentProfileDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.devspark.childcare.child.ChildRepository;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentProfileService parentProfileService;
    private final ChildRepository childRepository;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<ParentProfileDto> getProfile(Principal principal) {
        return ApiResponse.success("Parent profile retrieved", parentProfileService.getParentProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<String> updateProfile(@RequestBody ParentProfileDto dto, Principal principal) {
        parentProfileService.updateParentProfile(principal.getName(), dto);
        return ApiResponse.success("Parent profile updated successfully", null);
    }

    @GetMapping("/child/{childId}")
    @PreAuthorize("hasRole('PARENT')")
    public ApiResponse<com.devspark.childcare.auth.dto.ParentChildViewDto> getChildDetails(@PathVariable String childId, Principal principal) {
        ParentProfileDto profile = parentProfileService.getParentProfile(principal.getName());
        boolean ownsChild = profile.getChildren().stream().anyMatch(c -> c.getId().equals(childId));
        
        if (!ownsChild) {
            throw new RuntimeException("Access denied: This child is not linked to your account.");
        }

        com.devspark.childcare.child.Child child = childRepository.findById(childId).orElseThrow();
        
        return ApiResponse.success("Child details retrieved", com.devspark.childcare.auth.dto.ParentChildViewDto.builder()
                .childId(child.getChildId())
                .name(child.getFirstName() + " " + child.getLastName())
                .fullName(child.getFirstName() + " " + child.getLastName())
                .dob(child.getDob())
                .gender(child.getGender().name())
                .bloodGroup(child.getBloodGroup())
                .height(child.getHeight() != null ? child.getHeight().toString() : "N/A")
                .weight(child.getWeight() != null ? child.getWeight().toString() : "N/A")
                .address(profile.getAddress()) // Using parent's address
                .specialNote(child.getSpecialNote())
                .profileImage(child.getProfilePic())
                .enrolledDate(child.getCreatedAt().toLocalDate().toString())
                .build());
    }
}
