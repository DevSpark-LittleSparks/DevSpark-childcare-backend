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

    /**
     * Runs every hour (for demo purposes) to check for children who are 6 years or older.
     * These children are automatically marked as BIG_SCHOOL_READY.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void updateSchoolReadyStatus() {
        log.info("Running automatic child status check (Age 6+)...");
        
        // Children born 6 or more years ago
        LocalDate cutoff = LocalDate.now().minusYears(6);
        
        List<Child> eligibleChildren = childRepository.findAllByStatusNotAndDobBefore(
                ChildStatus.BIG_SCHOOL_READY, cutoff);

        if (!eligibleChildren.isEmpty()) {
            log.info("Found {} children eligible for BIG_SCHOOL_READY status", eligibleChildren.size());
            for (Child child : eligibleChildren) {
                // If they are already ALUMNI, we don't downgrade them
                if (child.getStatus() != ChildStatus.ALUMNI) {
                    child.setStatus(ChildStatus.BIG_SCHOOL_READY);
                    log.info("Updated status for child {} ({}) to BIG_SCHOOL_READY", 
                            child.getFirstName() + " " + child.getLastName(), child.getChildId());
                }
            }
            childRepository.saveAll(eligibleChildren);
        }
    }
}
