package com.example.bankcards.dto.response;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponseDTO {
    private Long id;
    private String maskedCardNumber;
    private String cardHolder;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardResponseDTO(Card card) {
        this.id = card.getId();
        this.cardHolder = card.getCardHolder();
        this.expiryDate = card.getExpiryDate();
        this.status = card.getStatus();
        this.balance = card.getBalance();

        this.maskedCardNumber = maskCardNumber(card.getCardNumber());
    }

    private String maskCardNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) {
            return fullNumber;
        }
        // Показываем только последние 4 цифры
        String lastFour = fullNumber.substring(fullNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
}