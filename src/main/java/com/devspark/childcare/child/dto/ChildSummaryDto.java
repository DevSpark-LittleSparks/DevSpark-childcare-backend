package com.devspark.childcare.child.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildSummaryDto {
    private UUID childId;
    private String name;
    private String profilePic;
    private String status;
    private String dob;
}
