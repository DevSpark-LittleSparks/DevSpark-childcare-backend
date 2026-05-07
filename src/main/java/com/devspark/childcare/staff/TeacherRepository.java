package com.devspark.childcare.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    java.util.Optional<Teacher> findByAccountEmail(String email);
    java.util.Optional<Teacher> findByAccountAccountId(String accountId);
}
