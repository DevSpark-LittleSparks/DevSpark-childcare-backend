package com.devspark.childcare.payment;

import com.devspark.childcare.auth.Parent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class CardDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cardDetailsId;

    private String cardHolderName;

    private String cardLast4;

    // Stripe's id for this tokenized payment method (pm_...) - the raw card
    // number never reaches this backend, only Stripe.js on the frontend sees it.
    @Column(unique = true)
    private String stripePaymentMethodId;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    private LocalDateTime expDate;

    private String paidVia;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;



    public enum CardType{
        VISA,
        MASTERCARD,
        AMEX,
        DISCOVER,
        UNKNOWN
    }
}
