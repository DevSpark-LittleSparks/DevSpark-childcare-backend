package com.devspark.childcare.comms;

import com.devspark.childcare.comms.dto.BroadcastRequestDto;
import com.devspark.childcare.comms.dto.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {

    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
                .targetRefId(dto.getTargetRefId()) // null = whole group, set = single recipient
                .build();

        notificationTargetRepository.save(target);

        log.info("Broadcast successful for target: {}{}", dto.getTargetType(),
                dto.getTargetRefId() != null ? " (individual: " + dto.getTargetRefId() + ")" : " (group)");

        NotificationResponseDto wsResponse = NotificationResponseDto.builder()
                .id(savedNotification.getNotificationId().toString())
                .title(savedNotification.getTitle())
                .body(savedNotification.getBody())
                .priority(savedNotification.getPriority())
                .type(savedNotification.getType())
                .isRead(false)
                .createdAt(savedNotification.getCreatedAt())
                .build();
                
        if (dto.getTargetType() == NotificationTarget.TargetType.ALL) {
            messagingTemplate.convertAndSend("/topic/broadcasts", wsResponse);
        } else {
            messagingTemplate.convertAndSend("/topic/" + dto.getTargetType().name().toLowerCase() + "s", wsResponse);
        }
    }
}
