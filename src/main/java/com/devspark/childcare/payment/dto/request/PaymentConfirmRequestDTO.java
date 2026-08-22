package com.devspark.childcare.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequestDTO {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    @NotBlank(message = "Stripe PaymentIntent ID is required")
    private String paymentIntentId;
}
