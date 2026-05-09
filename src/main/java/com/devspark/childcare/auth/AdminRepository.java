package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByAccountEmail(String email);
    Optional<Admin> findByAccountAccountId(UUID accountId);
}
