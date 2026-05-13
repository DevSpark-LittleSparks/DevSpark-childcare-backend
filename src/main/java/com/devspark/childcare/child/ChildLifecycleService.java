package com.devspark.childcare.child;

import com.devspark.childcare.comms.Notification;
import com.devspark.childcare.comms.NotificationRepository;
import com.devspark.childcare.comms.NotificationTarget;
import com.devspark.childcare.comms.NotificationTargetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildLifecycleService {

    private final ChildRepository childRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;

    /**
     * Runs every day at midnight to check for children reaching school age (6 years).
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkChildMilestones() {
        log.info("Starting daily child milestone check...");
        
        List<Child> enrolledChildren = childRepository.findByStatus(ChildStatus.ENROLLED);
        LocalDate today = LocalDate.now();

        for (Child child : enrolledChildren) {
            int age = Period.between(child.getDob(), today).getYears();
            
            if (age >= 6) {
                promoteToBigSchoolReady(child);
            }
        }
        
        log.info("Daily child milestone check completed.");
    }

    private void promoteToBigSchoolReady(Child child) {
        log.info("Promoting child {} {} (ID: {}) to BIG_SCHOOL_READY", 
                child.getFirstName(), child.getLastName(), child.getChildId());
        
        child.setStatus(ChildStatus.BIG_SCHOOL_READY);
        childRepository.save(child);

        // Create notification for parent
        String message = String.format(
            "🌟 Special Milestone! Your little spark, %s, has reached the age of 6! " +
            "They are now officially 'Big School Ready'. We are so proud of their journey with us! ❤️",
            child.getFirstName()
        );

        Notification notification = Notification.builder()
                .body(message)
                .build();
        notification = notificationRepository.save(notification);

        NotificationTarget target = NotificationTarget.builder()
                .notificationId(notification.getNotificationId())
                .targetType(NotificationTarget.TargetType.PARENT)
                .targetRefId(child.getParentId())
                .build();
        notificationTargetRepository.save(target);
    }
}
