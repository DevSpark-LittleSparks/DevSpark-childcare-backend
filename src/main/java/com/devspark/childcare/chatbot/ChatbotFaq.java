package com.devspark.childcare.chatbot;

import com.devspark.childcare.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "faq")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class ChatbotFaq extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "faq_id", updatable = false, nullable = false)
    private UUID faqId;

    @Column(length = 20)
    private String role; // NULL = applies to all roles

    @Column(nullable = false, length = 500)
    private String keywords; // comma-separated match keywords

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;
}
