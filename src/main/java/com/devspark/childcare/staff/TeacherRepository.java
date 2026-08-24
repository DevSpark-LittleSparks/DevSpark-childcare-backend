package com.devspark.childcare.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    Optional<Teacher> findByAccountEmail(String email);
    Optional<Teacher> findByAccountAccountId(UUID accountId);

    java.util.List<Teacher> findAllByDesignationAndCreatedAtBefore(Teacher.Designation designation, java.time.LocalDateTime dateTime);
}
