package com.example.bankcards.service.impl;

import com.example.bankcards.dto.response.UserResponseDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void getAllUsers_ReturnsPage() {
        User user = new User(); user.setId(1L); user.setUsername("test");
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<UserResponseDTO> result = userService.getAllUsers(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals("test", result.getContent().get(0).getUsername());
    }

    @Test
    void blockUser_SetsActiveFalse() {
        User user = new User(); user.setId(1L); user.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponseDTO res = userService.blockUser(1L);
        assertFalse(res.isActive());
    }

    @Test
    void activateUser_SetsActiveTrue() {
        User user = new User(); user.setId(1L); user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponseDTO res = userService.activateUser(1L);
        assertTrue(res.isActive());
    }

    @Test
    void blockUser_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.blockUser(99L));
    }
}