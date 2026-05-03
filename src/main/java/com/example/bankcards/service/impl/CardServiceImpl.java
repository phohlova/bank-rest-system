package com.example.bankcards.service.impl;

import com.example.bankcards.dto.response.CardResponseDTO;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Card getCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
    }

    @Override
    public Page<Card> getAllCardsByUser(Long userId, Pageable pageable, Optional<CardStatus> status) {
        if (status.isPresent()) {
            return cardRepository.findByUserIdAndStatus(userId, status.get(), pageable);
        } else {
            return cardRepository.findByUserId(userId, pageable);
        }
    }

    @Override
    public Card saveCard(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TransferResponse transferMoney(Long userId, String fromCardNumber, String toCardNumber, BigDecimal amount) {

        Card fromCard = cardRepository.findByCardNumber(fromCardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found: " + fromCardNumber));

        if (!fromCard.getUser().getId().equals(userId)) {
            throw new RuntimeException("Card does not belong to user");
        }

        Card toCard = cardRepository.findByCardNumber(toCardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found: " + toCardNumber));

        if (!toCard.getUser().getId().equals(userId)) {
            throw new RuntimeException("Destination card does not belong to user");
        }

        if (fromCard.getId().equals(toCard.getId())) {
            throw new RuntimeException("Cannot transfer to the same card");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Card is not active");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return new TransferResponse(
                UUID.randomUUID().toString(),
                maskCardNumber(fromCardNumber),
                maskCardNumber(toCardNumber),
                amount,
                LocalDateTime.now(),
                "COMPLETED"
        );
    }

    private String maskCardNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) {
            return fullNumber;
        }
        String lastFour = fullNumber.substring(fullNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    @Override
    public Page<CardResponseDTO> getAllCardsAdmin(Pageable pageable) {
        return cardRepository.findAll(pageable).map(CardResponseDTO::new);
    }
}