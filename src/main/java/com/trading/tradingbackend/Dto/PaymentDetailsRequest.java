package com.trading.tradingbackend.Dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetailsRequest {
    @NonNull
    private String accountNumber;
    @NonNull
    private String accountHolderName;
    @NonNull
    private String ifscCode;
    @NonNull
    private String bankName;
}
