package org.vullnet.vullnet00.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private String location;
    private String phone;
    private String skills;
    private boolean availability;
    private boolean profilePublic;
    private Integer rewardPoints;
    private Integer completedRequests;
    private Double averageRating;
    private Integer reviewCount;
}
