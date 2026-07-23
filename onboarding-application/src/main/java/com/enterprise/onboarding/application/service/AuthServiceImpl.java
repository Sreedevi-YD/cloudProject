package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.dto.auth.LoginRequest;
import com.enterprise.onboarding.application.dto.auth.LoginResponse;
import com.enterprise.onboarding.application.dto.auth.RegisterUserRequest;
import com.enterprise.onboarding.application.port.in.AuthService;
import com.enterprise.onboarding.application.exception.InvalidCredentialsException;
import com.enterprise.onboarding.application.port.out.PasswordHasher;
import com.enterprise.onboarding.application.port.out.TokenIssuer;
import com.enterprise.onboarding.domain.exception.DomainException;
import com.enterprise.onboarding.domain.model.RoleName;
import com.enterprise.onboarding.domain.model.User;
import com.enterprise.onboarding.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.isEnabled() || !passwordHasher.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        Set<String> roleNames = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        TokenIssuer.IssuedToken token = tokenIssuer.issue(user.getId(), user.getUsername(), roleNames);
        return new LoginResponse(token.token(), token.expiresInSeconds(), user.getUsername(), roleNames);
    }

    @Override
    public UUID register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DomainException("Username already taken: " + request.username());
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordHasher.hash(request.password()))
                .enabled(true)
                .roles(request.roles() == null || request.roles().isEmpty()
                        ? Set.of(RoleName.ROLE_EMPLOYEE)
                        : request.roles())
                .employeeId(request.employeeId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return userRepository.save(user).getId();
    }
}
