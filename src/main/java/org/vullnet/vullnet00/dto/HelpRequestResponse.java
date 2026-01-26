package org.vullnet.vullnet00.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpRequestResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String status;
    private Long ownerId;
    private String ownerName;
    private String ownerAvatar;
    private Long acceptedVolunteerId;
    private String acceptedVolunteerName;
    private String acceptedVolunteerPhone;
    private java.time.LocalDateTime completedAt;
    private String imageUrl;
    private String ownerPhone;
    private java.util.List<ApplicationResponse> applications;

}
