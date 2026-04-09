package org.vullnet.vullnet00.dto;

import lombok.Data;

@Data
public class NotificationBroadcastRequest {
    private String type;
    private String title;
    private String body;
    private String link;
}
