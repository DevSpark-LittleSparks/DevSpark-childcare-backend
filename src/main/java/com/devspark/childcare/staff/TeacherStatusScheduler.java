package com.devspark.childcare.staff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeacherStatusScheduler {

    private final TeacherRepository teacherRepository;

    /**
     * Runs every hour to check for teachers who have been "JUNIOR" for more than 24 hours.
     * These teachers are automatically promoted to "SENIOR".
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void promoteEligibleTeachers() {
        log.info("Running automatic teacher promotion check...");
        
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<Teacher> eligibleTeachers = teacherRepository.findAllByDesignationAndCreatedAtBefore(
                Teacher.Designation.JUNIOR, cutoff);

        if (!eligibleTeachers.isEmpty()) {
            log.info("Found {} teachers eligible for promotion to SENIOR", eligibleTeachers.size());
            for (Teacher teacher : eligibleTeachers) {
                teacher.setDesignation(Teacher.Designation.SENIOR);
                log.info("Promoted teacher {} ({}) to SENIOR", teacher.getFullName(), teacher.getTeacherId());
            }
            teacherRepository.saveAll(eligibleTeachers);
        }
    }
}
