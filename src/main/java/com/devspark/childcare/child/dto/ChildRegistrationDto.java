package com.devspark.childcare.child.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildRegistrationDto {
    private String fullName;
    private String nameWithInitials;
    private String dob;
    private String gender;
    private String bloodGroup;
    private Double height;
    private Double weight;
    private String address;
    private String specialNote;
    private String relationship;
    private String parentFullName;
    private String parentEmail;
    private String parentContact;
    private String parentID;
    private String profilePic;
}
