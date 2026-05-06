package com.devspark.childcare.auth;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "director")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "director_id", columnDefinition = "CHAR(36)")
    private String directorId;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "center_name")
    private String centerName;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "phone1")
    private String phone1;

    @Column(name = "phone2")
    private String phone2;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @PrePersist
    public void prePersist() {
        if (directorId == null) {
            directorId = UUID.randomUUID().toString();
        }
    }
}
