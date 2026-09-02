package com.devspark.childcare.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalChargeRequestDTO {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotBlank(message = "Charge type is required")
    private String chargeType; // REGISTRATION_FEE, FACILITY_FEE, OTHER

    private String description; // required only when chargeType is OTHER

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Long amount;
}
