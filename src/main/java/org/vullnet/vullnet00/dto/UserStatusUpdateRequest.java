package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusUpdateRequest {
    @NotNull
    private Boolean active;
}
