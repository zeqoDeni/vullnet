package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelpRequestCreateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
}
