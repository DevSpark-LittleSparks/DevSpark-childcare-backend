package com.devspark.childcare.child;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChildRepository extends JpaRepository<Child, UUID> {
    List<Child> findByParentId(UUID parentId);
    List<Child> findByStatus(ChildStatus status);
    boolean existsByFirstNameAndLastNameAndDobAndParentId(String firstName, String lastName, java.time.LocalDate dob, UUID parentId);
    List<Child> findAllByStatusNotAndDobBefore(ChildStatus status, java.time.LocalDate dobCutoff);
}
