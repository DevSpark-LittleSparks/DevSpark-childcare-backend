package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, String> {
    Optional<Director> findByAccountEmail(String email);
    Optional<Director> findByAccountAccountId(String accountId);
}
