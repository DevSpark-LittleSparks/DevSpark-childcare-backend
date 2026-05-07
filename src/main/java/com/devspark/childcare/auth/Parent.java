package com.devspark.childcare.auth;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

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
    @Column(name = "parent_id", columnDefinition = "CHAR(36)")
    private String parentId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false, columnDefinition = "CHAR(36)")
    private Account account;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    private String nic;

    @Column(name = "phone2", length = 20)
    private String phone2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Relationship relationship;

    @Column(name = "profile_picture", columnDefinition = "LONGTEXT")
    private String profilePicture;


    public enum Relationship {
        MOTHER, FATHER, GUARDIAN
    }
}
