package com.devspark.childcare.child;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, String> {

    /** Check if admin has pre-registered this email during admissions */
    boolean existsByGuardianEmail(String guardianEmail);

    /** Find all children linked to a guardian email (for parent signup validation) */
    List<Child> findByGuardianEmail(String guardianEmail);

    /** Link parent profile to child after OTP activation */
    List<Child> findByParentId(String parentId);

    boolean existsByFirstNameAndLastNameAndDobAndParentId(String firstName, String lastName, java.time.LocalDate dob, String parentId);
}
