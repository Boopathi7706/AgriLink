package com.agrilink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Temporary Spring Security configuration for Phase 1 development and infrastructure testing.
 *
 * NOTE: This configuration temporarily disables CSRF and permits all incoming HTTP requests
 * without authentication to unblock local development and verify Dockerized infrastructure.
 *
 * Proper JWT-based authentication and role-based authorization will replace this configuration
 * in subsequent development phases.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Temporarily disable CSRF for development
            .csrf(AbstractHttpConfigurer::disable)
            // Enable CORS with defaults from WebConfig
            .cors(Customizer.withDefaults())
            // Configure stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Temporarily permit all HTTP requests without requiring authentication
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
