package com.trading.tradingbackend.Dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordResponse {
    private String message;
    private boolean success;
}
