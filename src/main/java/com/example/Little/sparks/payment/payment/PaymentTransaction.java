package com.example.Little.sparks.payment.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID txnId;

    private LocalDateTime tnxTime;

    @Enumerated(EnumType.STRING)
    private TxnStatus status;

    private String gatewayReference;





    public enum TxnStatus {
        SUCCESSFUL,
        UNSUCCESSFUL
    }
}
