package com.devspark.childcare.auth.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentProfileDto {
    private String name;
    private String email;
    private String role;
    private String phone1;
    private String phone2;
    private String address;
    private String relationship;
    private String profileImage;
    private List<ChildSummaryDto> children;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildSummaryDto {
        private String id;
        private String name;
        private String profileImage;
    }
}
