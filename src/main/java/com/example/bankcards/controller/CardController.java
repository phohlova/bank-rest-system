package com.example.bankcards.controller;

import com.example.bankcards.dto.response.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
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
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) {
        Card card = cardService.getCardById(id);
        return ResponseEntity.ok(new CardResponseDTO(card));
    }

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