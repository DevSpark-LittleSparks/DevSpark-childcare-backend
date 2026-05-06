package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRegistrationRequestRepository extends JpaRepository<DirectorRegistrationRequest, String> {
    Optional<DirectorRegistrationRequest> findByEmail(String email);
}
