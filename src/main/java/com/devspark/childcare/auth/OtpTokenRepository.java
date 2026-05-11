package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {
    Optional<OtpToken> findByOtpCodeAndAccountEmailAndUsedFalse(String otpCode, String email);
    List<OtpToken> findByAccountEmail(String email);
}
