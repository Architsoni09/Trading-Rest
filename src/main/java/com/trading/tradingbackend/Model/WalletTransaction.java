package com.trading.tradingbackend.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Entity
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private Wallet wallet;

    private WALLET_TRANSACTION_TYPE transactionType;

    private LocalDate date;

    private String purpose;

    private Long receiverWalletId;

    private Long amount;
}
