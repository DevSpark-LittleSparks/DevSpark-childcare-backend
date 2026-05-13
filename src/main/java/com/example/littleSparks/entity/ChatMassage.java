package com.example.littleSparks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMassage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID messageId;

    private String content ;

    private LocalDate sentAt;

    private boolean isViewed;

    @ManyToOne
    @JoinColumn(name = "account_account_id")
    private Account account;


}
