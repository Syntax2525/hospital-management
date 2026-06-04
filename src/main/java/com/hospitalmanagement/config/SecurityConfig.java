package com.hospitalmanagement.config;

import com.hospitalmanagement.security.BearerTokenAuthenticationFilter;
import com.hospitalmanagement.security.UserActionAuditFilter;
import com.hospitalmanagement.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            BearerTokenAuthenticationFilter tokenFilter,
            UserActionAuditFilter auditFilter,
            ObjectMapper objectMapper
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, ex) ->
                                writeError(response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required."))
                        .accessDeniedHandler((request, response, ex) ->
                                writeError(response, objectMapper, HttpServletResponse.SC_FORBIDDEN, "Access Denied"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/*.html", "/styles/**", "/scripts/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/stats", "/api/notifications").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/notifications/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/settings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/settings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "NURSE", "BILLING")
                        .requestMatchers(HttpMethod.POST, "/api/patients/**").hasRole("RECEPTIONIST")
                        .requestMatchers(HttpMethod.PUT, "/api/patients/**").hasRole("RECEPTIONIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/patients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/clinical/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.POST, "/api/clinical/appointments/**").hasRole("RECEPTIONIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/clinical/appointments/**").hasRole("RECEPTIONIST")
                        .requestMatchers(HttpMethod.POST, "/api/clinical/triage").hasRole("NURSE")
                        .requestMatchers(HttpMethod.POST, "/api/clinical/consultations").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/clinical/lab-orders/**").hasAnyRole("ADMIN", "LAB_TECHNICIAN", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/clinical/lab-orders").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/clinical/lab-order-items/*/result").hasRole("LAB_TECHNICIAN")
                        .requestMatchers(HttpMethod.GET, "/api/clinical/wards", "/api/clinical/beds").hasAnyRole("NURSE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pharmacy/**").hasAnyRole("ADMIN", "PHARMACIST")
                        .requestMatchers("/api/pharmacy/**").hasRole("PHARMACIST")
                        .requestMatchers(HttpMethod.GET, "/api/billing/**").hasAnyRole("ADMIN", "BILLING")
                        .requestMatchers("/api/billing/**").hasRole("BILLING")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(auditFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    FilterRegistrationBean<UserActionAuditFilter> userActionAuditFilterRegistration(UserActionAuditFilter filter) {
        FilterRegistrationBean<UserActionAuditFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    private void writeError(HttpServletResponse response, ObjectMapper objectMapper, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(message));
    }
}
