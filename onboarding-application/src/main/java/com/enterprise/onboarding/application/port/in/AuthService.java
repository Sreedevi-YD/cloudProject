package com.enterprise.onboarding.application.port.in;

import com.enterprise.onboarding.application.dto.auth.LoginRequest;
import com.enterprise.onboarding.application.dto.auth.LoginResponse;
import com.enterprise.onboarding.application.dto.auth.RegisterUserRequest;

import java.util.UUID;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UUID register(RegisterUserRequest request);
}
