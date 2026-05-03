package com.example.bankcards.service.impl;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock private CardRepository cardRepository;
    @InjectMocks private CardServiceImpl cardService;

    @Test
    void transferMoney_Success() {
        User owner = new User(); owner.setId(1L);

        Card from = new Card();
        from.setId(10L);
        from.setUser(owner);
        from.setBalance(new BigDecimal("1000"));
        from.setStatus(CardStatus.ACTIVE);

        Card to = new Card();
        to.setId(20L);
        to.setUser(owner);
        to.setBalance(new BigDecimal("0"));
        to.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.of(from));
        when(cardRepository.findByCardNumber("2222")).thenReturn(Optional.of(to));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = cardService.transferMoney(1L, "1111", "2222", new BigDecimal("500"));

        assertEquals("COMPLETED", response.getStatus());
        assertEquals(new BigDecimal("500"), from.getBalance());
        assertEquals(new BigDecimal("500"), to.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferMoney_InsufficientFunds() {
        User owner = new User(); owner.setId(1L);

        Card from = new Card();
        from.setId(10L);
        from.setUser(owner);
        from.setBalance(new BigDecimal("100"));
        from.setStatus(CardStatus.ACTIVE);

        Card to = new Card();
        to.setId(20L);
        to.setUser(owner);
        to.setBalance(new BigDecimal("0"));
        to.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.of(from));
        when(cardRepository.findByCardNumber("2222")).thenReturn(Optional.of(to));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cardService.transferMoney(1L, "1111", "2222", new BigDecimal("200"))
        );
        assertEquals("Insufficient funds", exception.getMessage());
    }
}