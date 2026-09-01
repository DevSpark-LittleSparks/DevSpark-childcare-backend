package com.devspark.childcare.comms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, UUID> {
    List<NotificationTarget> findByTargetRefId(UUID targetRefId);
    List<NotificationTarget> findByTargetTypeAndTargetRefIdIsNull(NotificationTarget.TargetType targetType);
}
