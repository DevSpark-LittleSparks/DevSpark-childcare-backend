package com.devspark.childcare.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRegistrationRequestRepository extends JpaRepository<TeacherRegistrationRequest, UUID> {
    Optional<TeacherRegistrationRequest> findByEmail(String email);
    List<TeacherRegistrationRequest> findByStatus(TeacherRegistrationRequest.RequestStatus status);
}
