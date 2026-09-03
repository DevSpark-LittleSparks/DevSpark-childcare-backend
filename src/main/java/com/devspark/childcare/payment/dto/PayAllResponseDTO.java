package com.devspark.childcare.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayAllResponseDTO {

    private int paidCount;
    private long totalAmount;

    // Set when Stripe requires 3D Secure before the charge can complete - the
    // frontend confirms with clientSecret, then calls /pay-all/confirm.
    private boolean requiresAction;
    private String clientSecret;
}
