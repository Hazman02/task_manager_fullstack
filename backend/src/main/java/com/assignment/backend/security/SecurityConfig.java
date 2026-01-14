package com.assignment.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ JWT apps usually disable CSRF (we use token)
            .csrf(csrf -> csrf.disable())

            // ✅ no session (JWT = stateless)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ✅ authorization rules
            .authorizeHttpRequests(auth -> auth
                // allow CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // allow auth endpoints (register/login)
                .requestMatchers("/api/auth/**").permitAll()

                // protect everything else under /api
                .requestMatchers("/api/**").authenticated()

                // anything outside /api is allowed
                .anyRequest().permitAll()
            )

            // ✅ run JWT filter before Spring’s auth filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // ✅ disable default login methods
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
