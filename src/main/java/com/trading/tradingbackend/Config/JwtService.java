package com.trading.tradingbackend.Config;
import com.trading.tradingbackend.Model.Token;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.TokenRepository;
import com.trading.tradingbackend.Service.UserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserDetailService userDetailService;
    private final TokenRepository tokenRepository;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String extractTokenFromHeader(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, Long jwtExpiration) {
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    public String extractUserNameFromToken(String jwt) {
        Claims claims = getClaimsFromJwt(jwt);
        return claims.get("email", String.class);
    }

    public Boolean isTokenValid(String jwt, UserDetails userDetails) {
        Claims claims = getClaimsFromJwt(jwt);
        return userDetails.getUsername().equals(claims.getSubject()) &&
                !claims.getExpiration().before(new Date());
    }

    private Claims getClaimsFromJwt(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwt.replace("Bearer ", ""))
                .getBody();
    }

    public Token generateToken(HashMap<String, Object> claims, User user) {
        Token token = Token.builder()
                .token(buildToken(claims, user, jwtExpiration))
                .user(user)
                .build();
        return tokenRepository.save(token);
    }

    public String refreshToken(String jwt) {
        Claims claims = getClaimsFromJwt(jwt);
        // You might want to reset the issuedAt and expiration time
        claims.setIssuedAt(new Date(System.currentTimeMillis()));
        claims.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration));

        // Generate a new token with updated claims
        String refreshedToken = Jwts.builder()
                .setClaims(claims)
                .signWith(getSignInKey())
                .compact();

        return refreshedToken;
    }
}
