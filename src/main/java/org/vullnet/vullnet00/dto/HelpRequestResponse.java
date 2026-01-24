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

}
