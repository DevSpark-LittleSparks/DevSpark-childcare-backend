package com.devspark.childcare.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayAllRequestDTO {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotNull(message = "A saved payment method is required")
    private UUID savedCardId;
}
