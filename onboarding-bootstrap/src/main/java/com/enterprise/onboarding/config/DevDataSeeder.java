package com.enterprise.onboarding.config;

import com.enterprise.onboarding.application.dto.auth.RegisterUserRequest;
import com.enterprise.onboarding.application.port.in.AuthService;
import com.enterprise.onboarding.domain.model.RoleName;
import com.enterprise.onboarding.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

/** Dev/local convenience only: seeds a default admin so the API is usable without a manual bootstrap step. */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements CommandLineRunner {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@12345";

    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(DEFAULT_ADMIN_USERNAME)) {
            return;
        }
        authService.register(new RegisterUserRequest(
                DEFAULT_ADMIN_USERNAME,
                "admin@example.com",
                DEFAULT_ADMIN_PASSWORD,
                Set.of(RoleName.ROLE_ADMIN),
                null
        ));
        log.info("Seeded default admin user '{}' (dev profile only)", DEFAULT_ADMIN_USERNAME);
    }
}
