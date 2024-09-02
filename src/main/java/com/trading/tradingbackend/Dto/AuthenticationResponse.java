package com.trading.tradingbackend.Dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String jwtToken;
    private String message;
    private boolean status;
    private boolean isTwoFactorAuthEnabled;
    private String session;
}
