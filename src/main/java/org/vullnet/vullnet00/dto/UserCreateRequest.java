package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;


    @NotBlank
    @Size(min = 8, message = "Passuordi duhet te jet te pakten 8 shkronja")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Passuordi duhet te kete shkronja dhe numra")
    private String password; // raw password coming from client
}
