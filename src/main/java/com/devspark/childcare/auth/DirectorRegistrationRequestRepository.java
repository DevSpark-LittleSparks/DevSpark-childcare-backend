package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DirectorRegistrationRequestRepository extends JpaRepository<DirectorRegistrationRequest, UUID> {
    Optional<DirectorRegistrationRequest> findByEmail(String email);
    List<DirectorRegistrationRequest> findByStatus(DirectorRegistrationRequest.RequestStatus status);
}
