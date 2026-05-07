package com.devspark.childcare.auth.dto;

import com.devspark.childcare.child.Child;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminChildViewDto {
    private String childId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private String height;
    private String weight;
    private String address;
    private String specialNote;
    private String profileImage;
    
    // Parent Details
    private String parentFullName;
    private String parentEmail;
    private String parentContact;
    private String parentID;
    private String relationship;
}
