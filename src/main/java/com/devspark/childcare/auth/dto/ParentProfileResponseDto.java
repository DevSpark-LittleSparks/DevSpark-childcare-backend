package com.devspark.childcare.auth.dto;

import com.devspark.childcare.child.dto.ChildSummaryDto;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentProfileResponseDto {
    private UUID parentId;
    private String fullName;
    private String email;
    private String profilePicture;
    private String role;
    private String relationship;
    private String phone;
    private String nic;
    private String address;
    private List<ChildSummaryDto> children;
}
