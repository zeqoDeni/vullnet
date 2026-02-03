package org.vullnet.vullnet00.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String body;
    private String link;
    private boolean read;
    private LocalDateTime createdAt;
}
