package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRegistrationRequestRepository extends JpaRepository<ParentRegistrationRequest, String> {
    Optional<ParentRegistrationRequest> findByEmail(String email);
    List<ParentRegistrationRequest> findByStatus(ParentRegistrationRequest.RequestStatus status);
}

