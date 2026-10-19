package com.shopwave.shopwave.service;

import com.shopwave.shopwave.dto.LoginRequest;
import com.shopwave.shopwave.model.Users;
import com.shopwave.shopwave.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginRejectsIncorrectPassword() {
        Users user = new Users();
        user.setPasswordHash("encoded-password");
        user.setEnabled(true);
        when(userRepository.findByUserName("shopper")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(new LoginRequest("shopper", "wrong-password"))
        );
    }

    @Test
    void loginRejectsDisabledUserEvenWithCorrectPassword() {
        Users user = new Users();
        user.setPasswordHash("encoded-password");
        user.setEnabled(false);
        when(userRepository.findByUserName("shopper")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct-password", "encoded-password")).thenReturn(true);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(new LoginRequest("shopper", "correct-password"))
        );
    }
}
