package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AdminCreateCardRequest;
import com.example.bankcards.dto.response.CardResponseDTO;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Cards", description = "Administration of bank cards (CRUD, blocking)")
public class AdminCardController {

    private final CardService cardService;

    @Operation(
            summary = "Get all cards (Paginated)",
            description = "Returns a list of all cards in the system. Accessible only by Admin."
    )
    @GetMapping
    public ResponseEntity<Page<CardResponseDTO>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCardsAdmin(pageable));
    }

    @Operation(
            summary = "Block card",
            description = "Sets card status to BLOCKED."
    )
    @PatchMapping("/{id}/block")
    public ResponseEntity<CardResponseDTO> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    @Operation(
            summary = "Activate card",
            description = "Sets card status to ACTIVE."
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<CardResponseDTO> activateCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @Operation(
            summary = "Create card for a user",
            description = "Creates a new card for a specific user. Server generates card number and CVV securely."
    )
    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody @Valid AdminCreateCardRequest request) {
        return ResponseEntity.ok(cardService.createCardForUser(request));
    }

    @Operation(
            summary = "Delete card",
            description = "Permanently removes a card from the system."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCardAdmin(id);
        return ResponseEntity.noContent().build();
    }
}