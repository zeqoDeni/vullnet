package org.vullnet.vullnet00.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicStatsResponse {
    private long openRequests;
    private long activeVolunteers;
    private long completedRequests;
}
