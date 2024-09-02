package com.trading.tradingbackend.Exceptions;

import com.trading.tradingbackend.Dto.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class GlobalExceptionHandler {
    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<AuthenticationResponse> handleDisabledException(DisabledException ex) {
        AuthenticationResponse response = AuthenticationResponse.builder()
                .message(ex.getMessage())
                .status(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
