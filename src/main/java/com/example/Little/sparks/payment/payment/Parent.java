package com.example.Little.sparks.payment.payment;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "parent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Parent extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "parent_id", updatable = false, nullable = false)
    private UUID parentId; // Changed from String to UUID, removed CHAR(36)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false) // Removed the columnDefinition = "CHAR(36)" part
    private Account account;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    private String nic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Relationship relationship;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @OneToMany(mappedBy = "parent")
    private List<Payment> paymentList;

    @OneToMany(mappedBy = "parent")
    private List<CardDetails> cardList;

    public enum Relationship {
        MOTHER, FATHER, GUARDIAN
    }
}
