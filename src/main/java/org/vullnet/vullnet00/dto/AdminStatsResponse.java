package org.vullnet.vullnet00.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsResponse {
    private long users;
    private long requests;
    private long openRequests;
    private long applications;
}
