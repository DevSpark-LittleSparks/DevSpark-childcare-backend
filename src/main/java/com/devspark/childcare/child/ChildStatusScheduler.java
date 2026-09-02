package com.devspark.childcare.child;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChildStatusScheduler {

    private final ChildRepository childRepository;
    private final com.devspark.childcare.comms.NotificationRepository notificationRepository;
    private final com.devspark.childcare.comms.NotificationTargetRepository notificationTargetRepository;

    /**
     * Runs every hour (for demo purposes) to check for children who are 6 years or older.
     * These children are automatically marked as BIG_SCHOOL_READY.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void updateSchoolReadyStatus() {
        log.info("Running automatic child status checks...");
        
        // 1. Process 10+ year olds -> ALUMNI
        LocalDate age10Cutoff = LocalDate.now().minusYears(10);
        List<Child> alumniEligible = childRepository.findAllByStatusNotAndDobBefore(ChildStatus.ALUMNI, age10Cutoff);
        
        if (!alumniEligible.isEmpty()) {
            log.info("Found {} children eligible for ALUMNI status (10+)", alumniEligible.size());
            for (Child child : alumniEligible) {
                child.setStatus(ChildStatus.ALUMNI);
                log.info("Updated status for child {} to ALUMNI (10+)", child.getChildId());

                com.devspark.childcare.comms.Notification notif = com.devspark.childcare.comms.Notification.builder()
                        .title("Action Required: Child Reached Age 10")
                        .body(child.getFirstName() + " " + child.getLastName() + " is now 10 years old. Please review and remove them from the system if they are no longer attending.")
                        .priority(com.devspark.childcare.comms.Notification.Priority.HIGH)
                        .type(com.devspark.childcare.comms.Notification.Type.ADMIN_REQUEST)
                        .build();
                
                notificationRepository.save(notif);
            }
            childRepository.saveAll(alumniEligible);
        }

        // 2. Process 6+ year olds -> BIG_SCHOOL_READY
        LocalDate age6Cutoff = LocalDate.now().minusYears(6);
        List<Child> schoolAgeEligible = childRepository.findAllByStatusNotAndDobBefore(ChildStatus.BIG_SCHOOL_READY, age6Cutoff);

        if (!schoolAgeEligible.isEmpty()) {
            for (Child child : schoolAgeEligible) {
                // Skip if they are already ALUMNI
                if (child.getStatus() == ChildStatus.ALUMNI) continue;

                child.setStatus(ChildStatus.BIG_SCHOOL_READY);
                log.info("Updated status for child {} to BIG_SCHOOL_READY", child.getChildId());

                // Create Notification for the Parent
                if (child.getParentId() != null) {
                    com.devspark.childcare.comms.Notification notif = com.devspark.childcare.comms.Notification.builder()
                            .title("Child Reached School Age")
                            .body(child.getFirstName() + " has reached 'School Age' (6+) and has been marked to transition to half-time childcare.")
                            .priority(com.devspark.childcare.comms.Notification.Priority.HIGH)
                            .type(com.devspark.childcare.comms.Notification.Type.SYSTEM)
                            .build();
                    
                    notif = notificationRepository.save(notif);
                    
                    com.devspark.childcare.comms.NotificationTarget target = com.devspark.childcare.comms.NotificationTarget.builder()
                            .notificationId(notif.getNotificationId())
                            .targetType(com.devspark.childcare.comms.NotificationTarget.TargetType.PARENT)
                            .targetRefId(child.getParentId())
                            .build();
                    
                    notificationTargetRepository.save(target);
                }
            }
            childRepository.saveAll(schoolAgeEligible);
        }
    }
}
