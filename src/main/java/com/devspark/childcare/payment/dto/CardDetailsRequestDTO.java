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
public class CardDetailsRequestDTO {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    // Stripe.js tokenizes the raw card client-side and hands back this id -
    // the actual card number never reaches this backend.
    @NotBlank(message = "Stripe payment method ID is required")
    private String stripePaymentMethodId;
}
