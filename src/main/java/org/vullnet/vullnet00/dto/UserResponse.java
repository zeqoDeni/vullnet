package org.vullnet.vullnet00.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean active;
    private Integer rewardPoints;
    private Integer completedRequests;
    private Double averageRating;
    private Integer reviewCount;
    private String phone;
    private java.util.List<String> badges;

}
