package com.example.bankcards.controller;

import com.example.bankcards.dto.response.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<CardResponseDTO>> getMyCards() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Card> userCards = cardService.getAllCardsByUser(currentUser.getId());

        List<CardResponseDTO> response = userCards.stream()
                .map(CardResponseDTO::new)
                .toList();

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