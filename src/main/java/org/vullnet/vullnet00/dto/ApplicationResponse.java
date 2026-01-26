package org.vullnet.vullnet00.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Long applicationId;
    private Long helpRequestId;
    private Long applicantId;
    private String applicantName;
    private String status;
    private String message;
    private String applicantPhone;
}
