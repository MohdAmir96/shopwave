
package com.shopwave.shopwave.controller;

import com.shopwave.shopwave.dto.LoginRequest;
import com.shopwave.shopwave.dto.LoginResponse;
import com.shopwave.shopwave.dto.RegisterRequest;
import com.shopwave.shopwave.model.Users;
import com.shopwave.shopwave.service.AuthService;

import com.shopwave.shopwave.service.JwtService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    // Spring injects AuthService through this constructor.
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }

    // Handles POST requests to /api/auth/register.
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        // Delegate registration logic to the service.
        authService.register(request);

        // Return HTTP 201 Created when registration succeeds.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }



    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        // Validate the username and password.
        Users user = authService.login(request);

        // Generate a JWT for the authenticated user.
        String token = jwtService.generateToken(user);

        // Return the token to the client.
        LoginResponse response = new LoginResponse(token, "Bearer");

        return ResponseEntity.ok(response);
    }


}
