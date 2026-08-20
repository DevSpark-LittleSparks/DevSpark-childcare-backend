package com.example.Little.sparks.payment.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardDetailsRepository extends JpaRepository<CardDetails, UUID> {

    List<CardDetails> findByParent_ParentId(UUID parentId);
}
