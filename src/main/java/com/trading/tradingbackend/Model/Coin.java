package com.trading.tradingbackend.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cryptocurrency")
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coin {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "symbol", nullable = false)
    @JsonProperty("symbol")
    private String symbol;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @Column(name = "image")
    @JsonProperty("image")
    private String image;

    @Column(name = "current_price", nullable = false)
    @JsonProperty("current_price")
    private BigDecimal currentPrice;

    @Column(name = "market_cap", nullable = false)
    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @Column(name = "market_cap_rank", nullable = false)
    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @Column(name = "fully_diluted_valuation")
    @JsonProperty("fully_diluted_valuation")
    private BigDecimal fullyDilutedValuation;

    @Column(name = "total_volume", nullable = false)
    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    @Column(name = "high_24h")
    @JsonProperty("high_24h")
    private BigDecimal high24h;

    @Column(name = "low_24h")
    @JsonProperty("low_24h")
    private BigDecimal low24h;

    @Column(name = "price_change_24h")
    @JsonProperty("price_change_24h")
    private BigDecimal priceChange24h;

    @Column(name = "price_change_percentage_24h")
    @JsonProperty("price_change_percentage_24h")
    private BigDecimal priceChangePercentage24h;

    @Column(name = "market_cap_change_24h")
    @JsonProperty("market_cap_change_24h")
    private BigDecimal marketCapChange24h;

    @Column(name = "market_cap_change_percentage_24h")
    @JsonProperty("market_cap_change_percentage_24h")
    private BigDecimal marketCapChangePercentage24h;

    @Column(name = "circulating_supply")
    @JsonProperty("circulating_supply")
    private BigDecimal circulatingSupply;

    @Column(name = "total_supply")
    @JsonProperty("total_supply")
    private BigDecimal totalSupply;

    @Column(name = "max_supply")
    @JsonProperty("max_supply")
    private BigDecimal maxSupply;

    @Column(name = "ath")
    @JsonProperty("ath")
    private BigDecimal ath;

    @Column(name = "ath_change_percentage")
    @JsonProperty("ath_change_percentage")
    private BigDecimal athChangePercentage;

    @Column(name = "ath_date")
    @JsonProperty("ath_date")
    private LocalDateTime athDate;

    @Column(name = "atl")
    @JsonProperty("atl")
    private BigDecimal atl;

    @Column(name = "atl_change_percentage")
    @JsonProperty("atl_change_percentage")
    private BigDecimal atlChangePercentage;

    @Column(name = "atl_date")
    @JsonProperty("atl_date")
    private LocalDateTime atlDate;

    @Column(name = "roi")
    @JsonProperty("roi")
    @Embedded
    private Roi roi;

    @Column(name = "last_updated", nullable = false)
    @JsonProperty("last_updated")
    private LocalDateTime lastUpdated;

    @Data
    @NoArgsConstructor
    @Embeddable
    public static class Roi {
        @JsonProperty("times")
        private BigDecimal times;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("percentage")
        private BigDecimal percentage;
    }
}
