package com.shopwave.shopwave.config;

import com.shopwave.shopwave.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;                                  // NEW
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;                                  // NEW
import org.springframework.web.cors.CorsConfigurationSource;                            // NEW
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;                    // NEW

import java.util.List;                                                                  // NEW

@Configuration
@EnableMethodSecurity   // enables @PreAuthorize on controller/service methods (optional for now)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterServletRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    // NEW: tells browsers which frontend origins may call this API.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Must match the browser address exactly: scheme + host + port.
        // 5173 is Vite's default; use http://localhost:3000 for Create React App.
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // NEW: use the CorsConfigurationSource bean above. This also makes the
                // browser's preflight (OPTIONS) request succeed without a token.
                .cors(Customizer.withDefaults())

                // Disable CSRF because this API uses Bearer tokens,
                // rather than browser-managed session authentication.
                .csrf(AbstractHttpConfigurer::disable)

                // Disable Spring Security's default login mechanisms.
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Do not create or use HTTP sessions to store authentication.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Return 401 when a request is unauthenticated; keep 403 for
                // authenticated users who lack the required authority.
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )

                // Rules are checked top to bottom and the first match wins,
                // so the specific rules must come before anyRequest().
                .authorizeHttpRequests(auth -> auth
                        // Registration and login work without a JWT.
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // Only users with the ADMIN role (authority ROLE_ADMIN).
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Everything else needs a valid JWT.
                        .anyRequest().authenticated()
                )

                // Run our JWT filter before Spring Security's username/password
                // authentication filter.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}