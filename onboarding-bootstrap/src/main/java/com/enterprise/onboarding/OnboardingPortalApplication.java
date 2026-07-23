package com.enterprise.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Composition root. Placed at the package common to all four layers so Spring Boot's default
 * component/entity scanning covers domain, application, infrastructure and presentation without
 * explicit {@code @ComponentScan}/{@code @EntityScan} declarations.
 */
@SpringBootApplication
public class OnboardingPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnboardingPortalApplication.class, args);
    }
}
