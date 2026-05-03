package com.example.bankcards.service;

import com.example.bankcards.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDTO> getAllUsers(Pageable pageable);
}