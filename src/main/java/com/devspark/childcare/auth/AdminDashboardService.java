package com.devspark.childcare.auth;

import com.devspark.childcare.child.ChildRepository;
import com.devspark.childcare.comms.EmailService;
import com.devspark.childcare.staff.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final EmailService emailService;

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", childRepository.count());
        stats.put("totalParents", parentRepository.count());
        stats.put("totalStaff", teacherRepository.count());
        // For revenue, we could add a payment repository later. For now, let's mock it or leave it.
        return stats;
    }

    public void broadcastAnnouncement(String title, String content) {
        List<String> emails = parentRepository.findAllEmails();
        log.info("Broadcasting announcement '{}' to {} parents", title, emails.size());
        
        for (String email : emails) {
            try {
                emailService.sendAnnouncementEmail(email, title, content);
            } catch (Exception e) {
                log.error("Failed to send broadcast email to {}: {}", email, e.getMessage());
            }
        }
    }
}
