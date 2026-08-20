package com.devspark.childcare.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    private String billingMonth;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToOne
    private PaymentTransaction transaction;



    public enum PaymentStatus{
        PAYED,
         NOT_PAYED
     }


}
