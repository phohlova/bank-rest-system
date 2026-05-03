package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import java.util.List;

public interface CardService {

    Card getCardById(Long id);

    List<Card> getAllCardsByUser(Long userId);

    Card saveCard(Card card);

    void deleteCard(Long id);
}
