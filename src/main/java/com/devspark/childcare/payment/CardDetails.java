package com.devspark.childcare.payment;

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

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    private LocalDateTime expDate;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;



    public enum CardType{
        VISA,
        MASTERCARD
    }
}
