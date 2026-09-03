package com.devspark.childcare.payment;

import com.devspark.childcare.auth.Parent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    // What the charge is for - null for regular monthly billing (frontend
    // falls back to a generated "<Month> <Year>" label), set for one-off
    // additional charges (registration fee, facility fee, etc).
    private String description;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToOne
    private PaymentTransaction transaction;

    // When the charge was raised. billing_month is only month-granular, so
    // this is what "Recent Invoices" can actually order by.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;



    public enum PaymentStatus{
        PAYED,
         NOT_PAYED
     }


}
