package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String firstName;
    private String lastName;

    @Email
    private String email;

    // optional: only update if provided
    private String password;
}
