package com.devspark.childcare.auth;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Admin extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "admin_id", updatable = false, nullable = false)
    private UUID adminId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "profile_pic")
    private String profilePic;

    @Column(name = "phone1", length = 20)
    private String phone1;

    @Column(name = "phone2", length = 20)
    private String phone2;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "center_name", length = 150)
    private String centerName;

    @Column(name = "capacity", length = 50)
    private String capacity;
}
