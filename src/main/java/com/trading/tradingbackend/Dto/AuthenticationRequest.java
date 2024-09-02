package com.trading.tradingbackend.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotBlank(message = "No blank spaces should be there")
    @Email(message = "Email is not well-formed")
    private String email;
    @NotBlank(message = "No blank spaces should be there")
    @NotEmpty(message = "Password name is required")
    @Size(min = 8,message = "Password should be at least 8 characters")
    private String password;
}
