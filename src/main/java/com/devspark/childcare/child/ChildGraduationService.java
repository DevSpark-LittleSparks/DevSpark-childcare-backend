package com.devspark.childcare.child;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildGraduationService {

    private final ChildRepository childRepository;
    private final com.devspark.childcare.notification.NotificationRepository notificationRepository;
    private final com.devspark.childcare.auth.AccountRepository accountRepository;

    /**
     * Runs every day at midnight to check for children who reached age 6.
     * Cron: 0 0 0 * * *
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkChildGraduation() {
        log.info("Starting scheduled graduation check...");
        
        LocalDate sixYearsAgo = LocalDate.now().minusYears(6);
        
        // Find children who are 6 or older and still ENROLLED
        List<Child> eligibleForGraduation = childRepository.findAll().stream()
                .filter(c -> c.getStatus() == Child.Status.ENROLLED)
                .filter(c -> c.getDob().isBefore(sixYearsAgo) || c.getDob().isEqual(sixYearsAgo))
                .toList();

        for (Child child : eligibleForGraduation) {
            log.info("Child {} {} is eligible for graduation. Updating status.", child.getFirstName(), child.getLastName());
            child.setStatus(Child.Status.GRADUATING);
            childRepository.save(child);
            
            // Send attractive notification to Parent
            sendGraduationNotice(child);
        }
        
        log.info("Graduation check completed. {} children updated to GRADUATING.", eligibleForGraduation.size());
    }

    private void sendGraduationNotice(Child child) {
        if (child.getParentId() == null) return;

        // In a real system, we link Child -> Parent -> Account
        // Since we have parentId, let's find the parent record first
        // But for simplicity in this demo, let's look up the account by guardian email
        accountRepository.findByEmail(child.getGuardianEmail()).ifPresent(account -> {
            String title = "🚀 Ready for the Next Chapter, " + child.getFirstName() + "!";
            String message = "Time flies! ✨ " + child.getFirstName() + " has reached the wonderful age of 6. " +
                             "Our little spark has completed their journey at LittleSparks and is now ready for Grade 1! " +
                             "We are so proud of their growth. Please visit the office to collect their memories and discuss the next steps for school transition.";

            com.devspark.childcare.notification.Notification notification = com.devspark.childcare.notification.Notification.builder()
                    .account(account)
                    .title(title)
                    .message(message)
                    .type(com.devspark.childcare.notification.Notification.NotificationType.GRADUATION)
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
            log.info("Graduation notification sent to parent of {}", child.getFirstName());
        });
    }
}
