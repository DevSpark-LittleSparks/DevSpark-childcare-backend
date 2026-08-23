package com.devspark.childcare.settings;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettings {

    @Id
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "theme", nullable = false, length = 20)
    private String theme;

    @Column(name = "language", nullable = false, length = 20)
    private String language;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;
}
