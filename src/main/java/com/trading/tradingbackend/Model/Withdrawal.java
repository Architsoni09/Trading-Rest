package com.trading.tradingbackend.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.tradingbackend.Enums.WITHDRAWAL_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawl")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private WITHDRAWAL_STATUS withdrawalStatus;
    private Long amount;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    private LocalDateTime dateTime=LocalDateTime.now();
}
