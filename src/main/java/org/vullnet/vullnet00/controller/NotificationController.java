package org.vullnet.vullnet00.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.NotificationResponse;
import org.vullnet.vullnet00.dto.NotificationBroadcastRequest;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Page<NotificationResponse> list(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable
    ) {
        return notificationService.getForUser(principal.getId(), pageable);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long id
    ) {
        return notificationService.markRead(principal.getId(), id);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of("count", notificationService.countUnread(principal.getId()));
    }

    @PostMapping("/broadcast")
    public Map<String, String> broadcast(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody NotificationBroadcastRequest request
    ) {
        enforceAdmin(principal);
        notificationService.notifyAll(
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getLink()
        );
        return Map.of("status", "ok");
    }

    private void enforceAdmin(UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("E paautorizuar");
        }
        if (principal.getAuthorities().stream().anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))) {
            return;
        }
        throw new AccessDeniedException("E ndaluar");
    }
}
