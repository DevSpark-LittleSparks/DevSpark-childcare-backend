package com.devspark.childcare.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByParent_ParentId(UUID parentId);

    List<Payment> findByParent_ParentIdAndStatus(UUID parentId, Payment.PaymentStatus status);

    // Newest first - what the parent's invoice lists are actually meant to show.
    List<Payment> findByParent_ParentIdOrderByCreatedAtDesc(UUID parentId);

    List<Payment> findByParent_ParentIdAndStatusOrderByCreatedAtDesc(
            UUID parentId, Payment.PaymentStatus status);

    Optional<Payment> findByParent_ParentIdAndBillingMonth(UUID parentId, String billingMonth);

    boolean existsByParent_ParentIdAndBillingMonth(UUID parentId, String billingMonth);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    // Which payments a given Stripe PaymentIntent actually covered. Used to
    // finalize a "pay full balance" charge after 3D Secure without trusting a
    // client-supplied list of payment ids.
    List<Payment> findByParent_ParentIdAndStatusAndTransaction_GatewayReference(
            UUID parentId, Payment.PaymentStatus status, String gatewayReference);
}
