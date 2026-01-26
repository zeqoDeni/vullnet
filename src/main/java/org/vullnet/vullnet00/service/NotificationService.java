package org.vullnet.vullnet00.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vullnet.vullnet00.model.User;

@Service
@Slf4j
public class NotificationService {

    public void notifyEmail(User user, String subject, String body) {
        if (user == null || user.getEmail() == null) return;
        log.info("[EMAIL][{}] {} - {}", user.getEmail(), subject, body);
    }

    public void notifySms(User user, String message) {
        if (user == null || user.getPhone() == null) return;
        log.info("[SMS][{}] {}", user.getPhone(), message);
    }
}
