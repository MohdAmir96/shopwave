package com.shopwave.shopwave.security;

import com.shopwave.shopwave.model.Users;
import com.shopwave.shopwave.repository.UserRepository;
import com.shopwave.shopwave.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor   // generates the constructor for the two final fields below
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Read the Authorization header from the incoming request.
        String authHeader = request.getHeader("Authorization");

        // If no Bearer token is supplied, continue without authenticating.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove the "Bearer " prefix to get the actual JWT.
        String token = authHeader.substring(7);

        try {
            // Validate the token and extract the username.
            String username = jwtService.extractUsername(token);

            // Authenticate only if this request has not already been authenticated.
            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load the user so the role (and enabled flag) come from the database.
                Users user = userRepository.findByUserName(username).orElse(null);

                if (user != null && user.isEnabled()) {
                    // Spring's hasRole("ADMIN") looks for the authority "ROLE_ADMIN".
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.getUserName(),
                                    null,
                                    authorities
                            );

                    // Store the authenticated identity for this request.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                // If the user is missing or disabled, we leave the request
                // unauthenticated, so protected endpoints return 401.
            }

        } catch (io.jsonwebtoken.JwtException
                 | IllegalArgumentException exception) {

            // Reject invalid or expired JWTs.
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token"
            );
            return;
        }

        // Continue to the next filter or eventually the controller.
        filterChain.doFilter(request, response);
    }
}