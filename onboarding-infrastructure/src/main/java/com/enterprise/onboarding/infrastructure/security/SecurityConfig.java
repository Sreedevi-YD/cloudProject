package com.enterprise.onboarding.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // H2 console renders itself inside an iframe; the default X-Frame-Options: DENY
                // would otherwise blank it out. sameOrigin (not disabling frame options entirely)
                // keeps clickjacking protection everywhere else the app doesn't self-frame.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        // User provisioning is admin-only — self-registration would let anyone
                        // mint themselves a ROLE_ADMIN account otherwise.
                        .requestMatchers("/api/v1/auth/register").hasRole("ADMIN")
                        .requestMatchers("/actuator/health/**").permitAll()
                        // Static UI shell: plain HTML/CSS/JS served from resources/static, calling
                        // the API below with a JWT the browser holds client-side. The pages are
                        // public; every API call they make is still enforced by the rules below.
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        // "/swagger-ui.html" is springdoc's redirect entry point into "/swagger-ui/**";
                        // it doesn't match that pattern itself, so it needs listing separately.
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Only actually resolves under the "local" profile (H2ConsoleAutoConfiguration
                        // is conditional on spring.h2.console.enabled); harmless 404 elsewhere.
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/v1/employees/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/api/v1/onboarding-requests/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/api/v1/documents/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                        .requestMatchers("/api/v1/assets/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers("/api/v1/tasks/**").hasAnyRole("ADMIN", "HR", "IT", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/api/v1/audit-logs/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
