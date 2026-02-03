package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vullnet.vullnet00.dto.NotificationResponse;
import org.vullnet.vullnet00.model.Notification;
import org.vullnet.vullnet00.model.NotificationType;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.NotificationRepo;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepo notificationRepo;

    public void notifyEmail(User user, String subject, String body) {
        if (user == null || user.getEmail() == null) return;
        log.info("[EMAIL][{}] {} - {}", user.getEmail(), subject, body);
    }

    public void notifySms(User user, String message) {
        if (user == null || user.getPhone() == null) return;
        log.info("[SMS][{}] {}", user.getPhone(), message);
    }

    public void notifyInApp(User recipient, NotificationType type, String title, String body, String link) {
        if (recipient == null) return;
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .body(body)
                .link(link)
                .build();
        notificationRepo.save(notification);
    }

    public org.springframework.data.domain.Page<NotificationResponse> getForUser(Long userId, org.springframework.data.domain.Pageable pageable) {
        return notificationRepo.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public NotificationResponse markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepo.findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Njoftimi nuk u gjet"));
        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
        }
        return toResponse(notification);
    }

    public long countUnread(Long userId) {
        return notificationRepo.countByRecipientIdAndReadAtIsNull(userId);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType() != null ? notification.getType().name() : null)
                .title(notification.getTitle())
                .body(notification.getBody())
                .link(notification.getLink())
                .read(notification.getReadAt() != null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
