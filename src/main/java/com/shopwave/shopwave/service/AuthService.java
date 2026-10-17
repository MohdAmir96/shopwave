
package com.shopwave.shopwave.service;

// Data received from the registration API
import com.shopwave.shopwave.dto.LoginRequest;
import com.shopwave.shopwave.dto.RegisterRequest;

// Defines roles such as USER and ADMIN
import com.shopwave.shopwave.exception.EmailAlreadyExistsException;
import com.shopwave.shopwave.exception.UsernameAlreadyExistsException;
import com.shopwave.shopwave.model.Role;

// Entity that represents a user in the database
import com.shopwave.shopwave.model.Users;

// Repository used to query and save users
import com.shopwave.shopwave.repository.UserRepository;

// Used to hash passwords securely
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

// Marks this class as a Spring service
import org.springframework.stereotype.Service;

// Runs the method inside a database transaction
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor  // will create constructor
public class AuthService {

    // Dependencies required by this service.
    // Spring injects these objects through the constructor.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection:
    // Spring provides the repository and password encoder automatically.
//    public AuthService(
//            UserRepository userRepository,
//            PasswordEncoder passwordEncoder) {
//
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }


    // Registration logic runs inside a database transaction.
    @Transactional
    public Users register(RegisterRequest request) {

        // STEP 1: Check whether the email is already registered.
        // If it exists, stop registration.
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered");
        }

        // STEP 2: Check whether the username is already taken.
        if (userRepository.existsByUserName(request.userName())) {
            throw new UsernameAlreadyExistsException("Username already registered");
        }

        // STEP 3: Create a new Java Users object.
        // It has not been saved to the database yet.
        Users user = new Users();

        // STEP 4: Copy the username from the DTO to the entity.
        user.setUserName(request.userName());

        // STEP 5: Copy the email from the DTO to the entity.
        user.setEmail(request.email());

        // STEP 6: Hash the password before storing it.
        // Never save the plain-text password.
        user.setPasswordHash(
                passwordEncoder.encode(request.password()));

        // STEP 7: Every public registration creates a normal user.
        // The client cannot choose the role through RegisterRequest.
        user.setRole(Role.USER);

        // STEP 8: Save the user using Spring Data JPA.
        // PostgreSQL stores the record, and the saved entity is returned.
        return userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public Users login(LoginRequest request) {

        // STEP 1: Find the user by username.
        // If no user exists, stop and report an authentication failure.
        Users user = userRepository.findByUserName(request.userName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid username or password"));

        // STEP 2: Compare the submitted password with the stored BCrypt hash.
        // Never compare the raw password directly with the database hash.
        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException("Invalid username or password");
        }

        // STEP 3: Return the authenticated user's entity.
        return user;
    }

}
