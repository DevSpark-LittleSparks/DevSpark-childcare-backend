package com.devspark.childcare.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository for ActivityProgressLog entity.
 */
@Repository
public interface ActivityProgressLogRepository extends JpaRepository<ActivityProgressLog, UUID> {
    // Custom queries can be added here if needed
}