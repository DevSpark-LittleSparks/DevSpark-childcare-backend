package com.devspark.childcare.comms.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRecipientDto {
    private UUID accountId;
    private String name;
    private String email;
    private String role; // PARENT or TEACHER
}
