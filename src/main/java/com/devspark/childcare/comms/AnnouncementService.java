package com.devspark.childcare.comms;

import com.devspark.childcare.comms.dto.BroadcastRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {

    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;

    @Transactional
    public void broadcast(BroadcastRequestDto dto) {
        log.info("Broadcasting announcement: {}", dto.getTitle());

        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .body(dto.getBody())
                .priority(dto.getPriority())
                .type(Notification.Type.BROADCAST)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        NotificationTarget target = NotificationTarget.builder()
                .notificationId(savedNotification.getNotificationId())
                .targetType(dto.getTargetType())
                .targetRefId(null) // NULL for broadcast to whole group
                .build();

        notificationTargetRepository.save(target);

        // TODO: Integrate with WebSocket for real-time delivery
        log.info("Broadcast successful for target: {}", dto.getTargetType());
    }
}
