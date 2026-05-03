package com.example.bankcards.controller;

import com.example.bankcards.dto.response.UserResponseDTO;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Users", description = "Administration of users (List, Block/Unblock)")
public class AdminUserController {

    private final UserService userService;

    @Operation(
            summary = "Get all users (Paginated)",
            description = "Returns a list of all users (without passwords). Accessible only by Admin."
    )
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(
            summary = "Block user",
            description = "Disables user account (login will be forbidden)."
    )
    @PatchMapping("/{id}/block")
    public ResponseEntity<UserResponseDTO> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    @Operation(
            summary = "Activate user",
            description = "Enables user account (allows login)."
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }
}