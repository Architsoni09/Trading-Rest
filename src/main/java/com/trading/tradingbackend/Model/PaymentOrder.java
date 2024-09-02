package com.trading.tradingbackend.Model;

import com.trading.tradingbackend.Enums.PAYMENT_METHOD;
import com.trading.tradingbackend.Enums.PAYMENT_ORDER_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;
    private PAYMENT_ORDER_STATUS paymentStatus;
    private PAYMENT_METHOD paymentMethod;

    @ManyToOne
    private User user;



}
