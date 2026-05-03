package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AdminCreateCardRequest;
import com.example.bankcards.dto.response.CardResponseDTO;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardResponseDTO>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCardsAdmin(pageable));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<CardResponseDTO> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CardResponseDTO> activateCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody @Valid AdminCreateCardRequest request) {
        return ResponseEntity.ok(cardService.createCardForUser(request));
    }
}