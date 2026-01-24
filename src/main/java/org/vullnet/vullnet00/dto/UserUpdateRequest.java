package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(min = 1)
    private String firstName;
    @Size(min = 1)
    private String lastName;

    @Email
    private String email;

    // optional: only update if provided
    @Size(min = 8, message = "Passuordi duhet te jet te pakten 8 shkronja")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Passuordi duhet te kete shkronja dhe numra")
    private String password;
}
