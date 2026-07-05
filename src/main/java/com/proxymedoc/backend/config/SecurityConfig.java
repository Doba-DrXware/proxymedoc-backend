package com.proxymedoc.backend.config;

import com.proxymedoc.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    String body = "{ \"success\": false, \"message\": \"Unauthorized\" }";
                    try {
                        response.getWriter().write(body);
                    } catch (Exception ex) {
                        // fallback to sendError if writer fails
                        response.sendError(401, "Unauthorized");
                    }
                })
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/register/").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/login/").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/admin/reset-password", "/api/auth/admin/reset-password/").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pharmacies/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/pharmacies", "/api/pharmacies/{id}", "/api/pharmacies/search/name", "/api/pharmacies/search/nearby", "/api/pharmacies/validated", "/api/pharmacies/search", "/api/pharmacies/with-stocks", "/api/medicaments", "/api/medicaments/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers("/error").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

