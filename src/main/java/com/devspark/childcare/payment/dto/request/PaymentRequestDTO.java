package com.devspark.childcare.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Parent ID is required")
    private UUID parentId;

    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    // Card tokenization happens client-side via Stripe Elements - the card
    // must already be saved (see CardDetailsController) before it can be
    // charged, so this is always required.
    @NotNull(message = "A saved payment method is required")
    private UUID savedCardId;
}
