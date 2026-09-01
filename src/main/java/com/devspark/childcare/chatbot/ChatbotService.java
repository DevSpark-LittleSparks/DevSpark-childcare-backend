package com.devspark.childcare.chatbot;

import com.devspark.childcare.chatbot.dto.ChatMessageRequestDto;
import com.devspark.childcare.chatbot.dto.ChatMessageResponseDto;
import com.devspark.childcare.chatbot.dto.ChatUserContextDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotFaqRepository faqRepository;
    private final GroqClient groqClient;

    private static final String UNKNOWN_MARKER = "__UNKNOWN__";

    private static final Map<String, List<String>> SUGGESTED_QUESTIONS = Map.of(
            "PARENT", List.of(
                    "How do I check my child's daily progress?",
                    "How can I see my child's attendance?",
                    "What do the meal stats on the progress page mean?",
                    "How do I update my profile details?"
            ),
            "TEACHER", List.of(
                    "How do I mark a child's activity progress?",
                    "How do I take attendance for my class?",
                    "How do I log a child's meal consumption?",
                    "Where can I see my assigned activities for today?"
            ),
            "ADMIN", List.of(
                    "How do I view learning progress across all children?",
                    "How do I check today's activity list?",
                    "How do I view a specific child's report?",
                    "How do I manage teacher or parent accounts?"
            ),
            "GUEST", List.of(
                    "How do I log in?",
                    "How do I sign up as a teacher?",
                    "How do I sign up as a parent?",
                    "What is LittleSparks?"
            )
    );

    private String docs = "";

    @PostConstruct
    private void loadDocs() {
        try (InputStream in = new ClassPathResource("chatbot/devspark-docs.md").getInputStream()) {
            docs = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            docs = "";
        }
    }

    public ChatMessageResponseDto handle(ChatMessageRequestDto request) {
        String role = request.getContext() != null ? request.getContext().getRole() : null;
        String message = request.getMessage();

        Optional<ChatbotFaq> faqMatch = matchFaq(message, role);
        if (faqMatch.isPresent()) {
            return new ChatMessageResponseDto(true, faqMatch.get().getAnswer());
        }

        String systemPrompt = buildSystemPrompt(request.getContext());
        String aiReply = groqClient.chat(systemPrompt, request.getHistory(), message);

        if (aiReply == null || aiReply.isBlank() || aiReply.contains(UNKNOWN_MARKER)) {
            return new ChatMessageResponseDto(true, fallbackReply(role));
        }

        return new ChatMessageResponseDto(true, aiReply.trim());
    }

    private Optional<ChatbotFaq> matchFaq(String message, String role) {
        String lower = message.toLowerCase(Locale.ROOT);
        List<ChatbotFaq> candidates = faqRepository.findByRoleIsNullOrRole(role);

        ChatbotFaq best = null;
        int bestScore = 0;
        for (ChatbotFaq faq : candidates) {
            int score = 0;
            for (String keyword : faq.getKeywords().split(",")) {
                String k = keyword.trim().toLowerCase(Locale.ROOT);
                if (!k.isEmpty() && lower.contains(k)) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = faq;
            }
        }
        return bestScore > 0 ? Optional.of(best) : Optional.empty();
    }

    private String buildSystemPrompt(ChatUserContextDto ctx) {
        String role = ctx != null && ctx.getRole() != null ? ctx.getRole() : "GUEST";
        String name = ctx != null && ctx.getName() != null ? ctx.getName() : "there";
        String page = ctx != null && ctx.getCurrentPage() != null ? ctx.getCurrentPage() : "unknown";

        if ("GUEST".equalsIgnoreCase(role)) {
            return """
                    You are Sprouty, the assistant for DevSpark (LittleSparks), a childcare management system.
                    You are shown on the PUBLIC landing page to a visitor who has NOT logged in yet.

                    You may ONLY answer general questions using the documentation below about: what this
                    platform is, how to log in, how to sign up as a teacher, how to sign up as a parent
                    (including the Guardian Email process), and the first-time OTP verification step.

                    For ANY other question — anything about a specific dashboard, a child's data, attendance,
                    meals, activities, progress, payments, or any other account-specific feature — do NOT
                    answer it, even if the documentation below covers it. Instead, politely tell the visitor
                    that they'll need to log in (or sign up) first, and that they're welcome to ask you again
                    once they're inside their dashboard.

                    If the question is unrelated to this app entirely, respond with EXACTLY this and nothing
                    else: __UNKNOWN__

                    --- DOCUMENTATION ---
                    %s
                    """.formatted(docs);
        }

        return """
                You are Sprouty, the in-app assistant for DevSpark (LittleSparks), a childcare management system.
                The current user is named %s, logged in with role %s, currently viewing page %s.

                Only answer using the documentation provided below. Tailor your answers to what a %s can actually
                do in this system. If the user asks about a feature that belongs to a DIFFERENT role (for example a
                TEACHER asking how to access an ADMIN-only page), do NOT explain that feature. Instead, politely tell
                them that feature is only available from that other role's portal, and that they would need to log in
                through that portal to explore it. From here, you can only help with %s features.

                If the question is unrelated to this app, or you cannot find the answer anywhere in the documentation
                below, respond with EXACTLY this and nothing else: __UNKNOWN__

                --- DOCUMENTATION ---
                %s
                """.formatted(name, role, page, role, role, docs);
    }

    private String fallbackReply(String role) {
        List<String> suggestions = SUGGESTED_QUESTIONS.getOrDefault(role, List.of(
                "How do I use this dashboard?",
                "Where can I update my profile?"
        ));
        StringBuilder sb = new StringBuilder("I'm not sure about that one. Here are a few things you can ask me:\n");
        for (String q : suggestions) {
            sb.append("• ").append(q).append("\n");
        }
        return sb.toString().trim();
    }
}
