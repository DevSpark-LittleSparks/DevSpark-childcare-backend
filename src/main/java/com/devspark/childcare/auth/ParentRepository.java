package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {
    Optional<Parent> findByAccountAccountId(UUID accountId);
    Optional<Parent> findByAccountEmail(String email);
    Optional<Parent> findByAccount_AccountId(UUID accountId);
}
