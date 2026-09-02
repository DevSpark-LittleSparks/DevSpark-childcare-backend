package com.devspark.childcare.payment.dto.response;

import com.devspark.childcare.payment.Payment;
import com.devspark.childcare.payment.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private UUID paymentId;
    private String billingMonth;
    private String description;
    private Long amount;
    private Payment.PaymentStatus status;
    private UUID parentId;
    private String parentName;

    // Transaction details (present only after payment is processed)
    private UUID txnId;
    private LocalDateTime txnTime;
    private PaymentTransaction.TxnStatus txnStatus;
    private String gatewayReference;

    // Set when Stripe requires additional customer action (3D Secure) before
    // the payment can complete - the frontend uses clientSecret with
    // stripe.confirmCardPayment(), then calls /confirm.
    private boolean requiresAction;
    private String clientSecret;
}
