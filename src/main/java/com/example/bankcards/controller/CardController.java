package com.example.bankcards.controller;

import com.example.bankcards.dto.response.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "User Cards", description = "Endpoints for managing the authenticated user's own cards")
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Get card details by ID",
            description = "Returns details of a specific card. Card number is masked for security.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card found"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) {
        Card card = cardService.getCardById(id);
        return ResponseEntity.ok(new CardResponseDTO(card));
    }

    @Operation(
            summary = "Get my cards",
            description = "Returns a paginated list of cards belonging to the authenticated user. Supports filtering by status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of cards retrieved"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @GetMapping("/my")
    public ResponseEntity<Page<CardResponseDTO>> getMyCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CardStatus status) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cardPage = cardService.getAllCardsByUser(
                currentUser.getId(),
                pageable,
                Optional.ofNullable(status)
        );

        Page<CardResponseDTO> response = cardPage.map(CardResponseDTO::new);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new card",
            description = "Creates a new bank card linked to the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody Card card) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        card.setUser(currentUser);
        Card savedCard = cardService.saveCard(card);

        return ResponseEntity.ok(new CardResponseDTO(savedCard));
    }
}