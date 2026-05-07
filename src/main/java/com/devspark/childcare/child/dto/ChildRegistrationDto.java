package com.devspark.childcare.child.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ChildRegistrationDto {
    private String fullName;
    private String nameWithInitials;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private BigDecimal height;
    private BigDecimal weight;
    private String address;
    private String specialNote;
    private String parentFullName;
    private String parentEmail;
    private String parentContact;
    private String parentID;
    private String relationship;
    private String profilePic;
}
