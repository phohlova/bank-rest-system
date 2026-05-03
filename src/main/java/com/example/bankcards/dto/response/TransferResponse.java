package com.example.bankcards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String transactionId;
    private String fromCardMasked;
    private String toCardMasked;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String status;
}