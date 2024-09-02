package com.trading.tradingbackend.Dto;

import com.trading.tradingbackend.Enums.ORDER_TYPE;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private ORDER_TYPE orderType;
}
