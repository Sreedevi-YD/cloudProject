package com.enterprise.onboarding.presentation.controller;

import com.enterprise.onboarding.application.dto.auth.LoginRequest;
import com.enterprise.onboarding.application.dto.auth.LoginResponse;
import com.enterprise.onboarding.application.dto.auth.RegisterUserRequest;
import com.enterprise.onboarding.application.port.in.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, UUID>> register(@Valid @RequestBody RegisterUserRequest request) {
        UUID id = authService.register(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + id))
                .body(Map.of("id", id));
    }
}
