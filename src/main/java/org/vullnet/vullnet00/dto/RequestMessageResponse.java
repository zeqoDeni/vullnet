package org.vullnet.vullnet00.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String body;
    private Instant createdAt;
}
