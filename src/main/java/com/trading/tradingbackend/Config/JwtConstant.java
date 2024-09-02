package com.trading.tradingbackend.Config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Data
public class JwtConstant {
    @Value("${application.security.jwt.secret-key}")
    public static String secretKey;
    @Value("${application.security.jwt.expiration}")
    public static final Long jwtExpiration = 60 * 60L;
    public static final String JwtHeader="Authorization";
}
