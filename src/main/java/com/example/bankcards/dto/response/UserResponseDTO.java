package com.example.bankcards.dto.response;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private boolean active;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.isActive();
    }
}