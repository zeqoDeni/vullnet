package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.AuthLoginRequest;
import org.vullnet.vullnet00.dto.AuthRegisterRequest;
import org.vullnet.vullnet00.dto.AuthResponse;
import org.vullnet.vullnet00.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }
}
