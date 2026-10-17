
package com.shopwave.shopwave.security;

import com.shopwave.shopwave.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

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
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // Create an authentication object for Spring Security.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of()
                        );

                // Store the authenticated identity for this request.
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
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
