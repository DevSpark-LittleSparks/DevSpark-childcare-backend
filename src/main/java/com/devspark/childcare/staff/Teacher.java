package com.devspark.childcare.staff;

import com.devspark.childcare.auth.Account;
import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Teacher extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "teacher_id", columnDefinition = "CHAR(36)")
    private String teacherId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, columnDefinition = "CHAR(36)")
    private Account account;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Designation designation;

    @Column(name = "max_daily_activities", nullable = false)
    private int maxDailyActivities;


    public enum Designation {
        SENIOR, JUNIOR
    }
}
