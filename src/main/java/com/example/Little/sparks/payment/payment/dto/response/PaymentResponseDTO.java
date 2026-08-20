package com.example.Little.sparks.payment.payment.dto.response;

import com.example.Little.sparks.payment.payment.Payment;
import com.example.Little.sparks.payment.payment.PaymentTransaction;
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
    private Long amount;
    private Payment.PaymentStatus status;
    private UUID parentId;
    private String parentName;

    // Transaction details (present only after payment is processed)
    private UUID txnId;
    private LocalDateTime txnTime;
    private PaymentTransaction.TxnStatus txnStatus;
    private String gatewayReference;
}
