package com.example.bankcards.service;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.AuthServiceImpl;
import com.example.bankcards.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @InjectMocks private AuthServiceImpl authService;

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("test"); req.setEmail("t@t.com"); req.setPassword("pass");

        User savedUser = new User();
        savedUser.setUsername("test");
        savedUser.setPassword("hashed");
        savedUser.setActive(true);

        when(userRepository.existsByEmail("t@t.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        when(jwtUtils.generateToken(any())).thenReturn("mock-token");

        AuthResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("mock-token", res.getToken());
        assertEquals("test", res.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UserExists_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("exists@t.com");

        when(userRepository.existsByEmail("exists@t.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void login_Success() {
        User user = new User();
        user.setUsername("test"); user.setPassword("hashed"); user.setActive(true);

        LoginRequest req = new LoginRequest("test", "pass");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        when(jwtUtils.generateToken(any())).thenReturn("jwt-token");

        AuthResponse res = authService.login(req);

        assertEquals("jwt-token", res.getToken());
        assertEquals("test", res.getUsername());
    }

    @Test
    void login_InvalidPassword_ThrowsBadCredentials() {
        User user = new User(); user.setUsername("test"); user.setPassword("hashed");
        LoginRequest req = new LoginRequest("test", "wrong");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void login_DisabledUser_ThrowsDisabledException() {
        User user = new User(); user.setUsername("test"); user.setPassword("hashed"); user.setActive(false);
        LoginRequest req = new LoginRequest("test", "pass");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);

        assertThrows(DisabledException.class, () -> authService.login(req));
    }
}