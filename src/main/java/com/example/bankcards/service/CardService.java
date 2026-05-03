package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CardService {

    Card getCardById(Long id);

    Page<Card> getAllCardsByUser(Long userId, Pageable pageable);

    Card saveCard(Card card);

    void deleteCard(Long id);
}
