package com.devspark.childcare.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository for Activity entity.
 * Provides out-of-the-box CRUD operations.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    // Custom queries (e.g., finding activities by category) can be added here later
}