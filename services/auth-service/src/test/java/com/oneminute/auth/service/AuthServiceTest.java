package com.oneminute.auth.service;

import com.oneminute.auth.dto.AuthResponse;
import com.oneminute.auth.dto.LoginRequest;
import com.oneminute.auth.dto.RegisterRequest;
import com.oneminute.auth.entity.User;
import com.oneminute.auth.exception.EmailAlreadyTakenException;
import com.oneminute.auth.exception.InvalidCredentialsException;
import com.oneminute.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("pass")
                .build();
        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("hashed");
        given(jwtService.generateToken(any(User.class))).willReturn("token");

        AuthResponse response = authService.register(request);

        assertEquals("token", response.getToken());
    }

    @Test
    void registerEmailTaken() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("pass")
                .build();
        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        assertThrows(EmailAlreadyTakenException.class, () -> authService.register(request));
    }

    @Test
    void authenticateSuccess() {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("pass")
                .build();
        User user = User.builder()
                .email(request.getEmail())
                .password("hashed")
                .role(User.Role.USER)
                .build();
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(true);
        given(jwtService.generateToken(user)).willReturn("token");

        AuthResponse response = authService.authenticate(request);

        assertEquals("token", response.getToken());
    }

    @Test
    void authenticateInvalidCredentials() {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("pass")
                .build();
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void authenticateWrongPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("wrong")
                .build();
        User user = User.builder()
                .email(request.getEmail())
                .password("hashed")
                .role(User.Role.USER)
                .build();
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
    }
}
