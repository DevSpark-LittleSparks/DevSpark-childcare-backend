package com.devspark.childcare.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, String> {
    Optional<OtpToken> findByOtpCodeAndAccountEmailAndUsedFalse(String otpCode, String email);
    java.util.List<OtpToken> findByAccountEmail(String email);
}
