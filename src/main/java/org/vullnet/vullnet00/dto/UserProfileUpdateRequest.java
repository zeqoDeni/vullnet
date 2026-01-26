package org.vullnet.vullnet00.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {
    @Size(max = 500)
    private String bio;
    @Size(max = 500)
    private String avatarUrl;
    @Size(max = 120)
    private String location;
    @Size(max = 40)
    private String phone;
    @Size(max = 500)
    private String skills;
    private Boolean availability;
    private Boolean profilePublic;
}
