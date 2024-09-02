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
public class RegistrationRequest {
    @NotEmpty(message = "User name is required")
    @NotBlank(message = "No blank spaces should be there")
    private String userName;
    @NotEmpty(message = "Last name is required")
    @Size(min = 10,message = "Mobile Number should be at least 10 characters")
    @NotBlank(message = "No blank spaces should be there")
    private String mobileNumber;
    @NotBlank(message = "No blank spaces should be there")
    @Email(message = "Email is not well-formed")
    private String email;
    @NotBlank(message = "No blank spaces should be there")
    @NotEmpty(message = "Password name is required")
    @Size(min = 8,message = "Password should be at least 8 characters")
    private String password;
    private Boolean isTwoFactorEnabled;
}

