package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationCreateRequest {
    @NotBlank
    private String message;
}
