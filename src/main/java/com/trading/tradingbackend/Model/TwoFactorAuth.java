package com.trading.tradingbackend.Model;

import com.trading.tradingbackend.Enums.VERIFICATION_TYPE;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorAuth {
    private VERIFICATION_TYPE verificationType;
    private Boolean twoFactorEnabled=false;
    private Boolean isUserVerified=false;
}
