package com.enterprise.onboarding.config;

import com.enterprise.onboarding.application.dto.auth.RegisterUserRequest;
import com.enterprise.onboarding.application.port.in.AuthService;
import com.enterprise.onboarding.domain.model.RoleName;
import com.enterprise.onboarding.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Creates the first admin account on a fresh deployment. Config-driven (not profile-gated) so it
 * works the same way everywhere: {@code dev}/{@code local} enable it with known defaults for
 * convenience; other profiles default to disabled and require an operator to set
 * {@code BOOTSTRAP_ADMIN_ENABLED=true} plus real credentials for the one-time first run — this
 * exists because {@code /api/v1/auth/register} is admin-only, so without it nothing could ever
 * create the very first admin.
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(BootstrapAdminProperties.class)
@Slf4j
public class AdminBootstrapSeeder implements CommandLineRunner {

    private final BootstrapAdminProperties properties;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (!properties.enabled()) {
            return;
        }
        if (userRepository.existsByUsername(properties.username())) {
            return;
        }
        authService.register(new RegisterUserRequest(
                properties.username(),
                properties.email(),
                properties.password(),
                Set.of(RoleName.ROLE_ADMIN),
                null
        ));
        log.info("Bootstrapped initial admin user '{}'. Disable app.security.bootstrap-admin.enabled "
                + "once you've created your real admin accounts.", properties.username());
    }
}
