package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface CardService {

    Card getCardById(Long id);

    Page<Card> getAllCardsByUser(Long userId, Pageable pageable, Optional<CardStatus> status);

    Card saveCard(Card card);

    void deleteCard(Long id);
}
