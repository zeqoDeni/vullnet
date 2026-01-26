package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateRequest {
    @NotBlank
    private String role;
}
