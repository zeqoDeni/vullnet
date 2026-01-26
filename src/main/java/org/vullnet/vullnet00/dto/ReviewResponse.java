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
public class ReviewResponse {
    private Long id;
    private Long helpRequestId;
    private Long reviewerId;
    private String reviewerName;
    private Long revieweeId;
    private String revieweeName;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}
