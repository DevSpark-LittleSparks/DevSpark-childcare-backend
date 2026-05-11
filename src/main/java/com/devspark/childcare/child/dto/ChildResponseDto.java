package com.devspark.childcare.child.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildResponseDto {
    private UUID childId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private String profilePic;
    private Double height;
    private Double weight;
    private String specialNote;
    private String address;
    private String relationship;
    private String parentContact;
    private String parentID;
    private String guardianName;
    private String guardianEmail;
    private String status;
}
