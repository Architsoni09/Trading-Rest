package com.trading.tradingbackend.Dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeminiPromptResponse {
    private String answer;
}
