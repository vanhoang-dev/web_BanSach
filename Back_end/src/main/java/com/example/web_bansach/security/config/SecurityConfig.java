package com.example.web_bansach.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.web_bansach.security.jwt.JwtAuthenticationFilter;
import com.example.web_bansach.security.jwt.JwtTokenProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws/**", "/app/**", "/topic/**", "/queue/**").permitAll()

                        // ============ PUBLIC ENDPOINTS (No Authentication) ============
                        .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINTS).permitAll()

                        // ============ USER ENDPOINTS (USER or ADMIN role) ============
                        .requestMatchers(HttpMethod.GET, Endpoints.USER_GET_ENDPOINTS).hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST, Endpoints.USER_POST_ENDPOINTS).hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PUT, Endpoints.USER_PUT_ENDPOINTS).hasAnyRole("USER")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.USER_DELETE_ENDPOINTS).hasAnyRole("USER")

                        // ============ ADMIN ENDPOINTS (ADMIN role only) ============
                        .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_GET_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, Endpoints.ADMIN_PUT_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.ADMIN_DELETE_ENDPOINTS).hasRole("ADMIN")

                        // ============ ALL OTHER REQUESTS REQUIRE AUTHENTICATION ============
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
