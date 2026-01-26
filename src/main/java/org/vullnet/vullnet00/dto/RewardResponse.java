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
public class RewardResponse {
    private Integer points;
    private Integer completedRequests;
    private Double averageRating;
    private Integer reviewCount;
}
