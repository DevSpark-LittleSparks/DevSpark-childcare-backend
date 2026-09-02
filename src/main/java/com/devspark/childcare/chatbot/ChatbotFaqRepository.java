package com.devspark.childcare.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatbotFaqRepository extends JpaRepository<ChatbotFaq, UUID> {
    List<ChatbotFaq> findByRoleIsNullOrRole(String role);
}
