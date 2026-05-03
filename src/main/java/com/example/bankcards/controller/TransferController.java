package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TransferResponse> transferMoney(@RequestBody @Valid TransferRequest request) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TransferResponse response = cardService.transferMoney(
                currentUser.getId(),
                request.getFromCardNumber(),
                request.getToCardNumber(),
                request.getAmount()
        );

        return ResponseEntity.ok(response);
    }
}