package com.trading.tradingbackend.Dto;

import com.trading.tradingbackend.Enums.WALLET_TRANSACTION_TYPE;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransactionRequest {

    private WALLET_TRANSACTION_TYPE transactionType;

    private LocalDate date;

    private String purpose;

    private Long receiverWalletId;

    private Long amount;
}
