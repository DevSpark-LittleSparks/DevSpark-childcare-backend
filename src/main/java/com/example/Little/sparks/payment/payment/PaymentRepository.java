package com.example.Little.sparks.payment.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByParent_ParentId(UUID parentId);

    List<Payment> findByParent_ParentIdAndStatus(UUID parentId, Payment.PaymentStatus status);

    Optional<Payment> findByParent_ParentIdAndBillingMonth(UUID parentId, String billingMonth);

    boolean existsByParent_ParentIdAndBillingMonth(UUID parentId, String billingMonth);
}
