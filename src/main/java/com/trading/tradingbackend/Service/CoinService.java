package com.trading.tradingbackend.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.tradingbackend.Model.Coin;
import com.trading.tradingbackend.Repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinRepository coinRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public List<Coin> getCoinList(int page) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=20&page=" + page;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            List<Coin> coinList = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<Coin>>() {});
            return coinList;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getMarketChart(String coinId, int days) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getCoinDetails(String coinId) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

            // Map JSON data to Coin entity
            Coin coin = new Coin();
            coin.setId(rootNode.get("id").asText());
            coin.setSymbol(rootNode.get("symbol").asText());
            coin.setName(rootNode.get("name").asText());
            coin.setImage(rootNode.path("image").path("large").asText("")); // Adjust default value if necessary

            // Nested data parsing
            JsonNode marketDataNode = rootNode.path("market_data");
            coin.setCurrentPrice(parseBigDecimal(marketDataNode, "current_price", "usd"));
            coin.setMarketCap(parseBigDecimal(marketDataNode, "market_cap", "usd"));
            coin.setMarketCapRank(rootNode.path("market_cap_rank").asInt());
            coin.setFullyDilutedValuation(parseBigDecimal(marketDataNode, "fully_diluted_valuation", "usd"));
            coin.setTotalVolume(parseBigDecimal(marketDataNode, "total_volume", "usd"));
            coin.setHigh24h(parseBigDecimal(marketDataNode, "high_24h", "usd"));
            coin.setLow24h(parseBigDecimal(marketDataNode, "low_24h", "usd"));
            coin.setPriceChange24h(parseBigDecimal(marketDataNode, "price_change_24h"));
            coin.setPriceChangePercentage24h(parseBigDecimal(marketDataNode, "price_change_percentage_24h"));
            coin.setMarketCapChange24h(parseBigDecimal(marketDataNode, "market_cap_change_24h"));
            coin.setMarketCapChangePercentage24h(parseBigDecimal(marketDataNode, "market_cap_change_percentage_24h"));
            coin.setCirculatingSupply(parseBigDecimal(marketDataNode, "circulating_supply"));
            coin.setTotalSupply(parseBigDecimal(marketDataNode, "total_supply"));
            coin.setMaxSupply(parseBigDecimal(marketDataNode, "max_supply"));
            coin.setAth(parseBigDecimal(marketDataNode, "ath", "usd"));
            coin.setAthChangePercentage(parseBigDecimal(marketDataNode, "ath_change_percentage"));
            coin.setAthDate(parseDateTime(marketDataNode, "ath_date"));
            coin.setAtl(parseBigDecimal(marketDataNode, "atl", "usd"));
            coin.setAtlChangePercentage(parseBigDecimal(marketDataNode, "atl_change_percentage"));
            coin.setAtlDate(parseDateTime(marketDataNode, "atl_date"));

            // Handle ROI
            JsonNode roiNode = rootNode.path("roi");
            if (!roiNode.isMissingNode()) {
                Coin.Roi roi = new Coin.Roi();
                roi.setTimes(parseBigDecimal(roiNode, "times"));
                roi.setCurrency(roiNode.path("currency").asText());
                roi.setPercentage(parseBigDecimal(roiNode, "percentage"));
                coin.setRoi(roi);
            }

            coin.setLastUpdated(parseDateTime(rootNode, "last_updated"));
            coinRepository.save(coin);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    private BigDecimal parseBigDecimal(JsonNode node, String fieldName) {
        return parseBigDecimal(node, fieldName, null);
    }

    private BigDecimal parseBigDecimal(JsonNode node, String fieldName, String currency) {
        JsonNode fieldNode = currency == null ? node.path(fieldName) : node.path(fieldName).path(currency);
        if (fieldNode.isMissingNode() || fieldNode.isNull() || fieldNode.asText().trim().isEmpty()) {
            return null; // Handle missing, null, or empty values
        }
        try {
            return new BigDecimal(fieldNode.asText());
        } catch (NumberFormatException e) {
            // Handle parsing error, log it, or set a default value if needed
            System.err.println("NumberFormatException: " + e.getMessage());
            return null; // Or handle the error as required
        }
    }

    private LocalDateTime parseDateTime(JsonNode node, String fieldName) {
        return parseDateTime(node.path(fieldName));
    }

    private LocalDateTime parseDateTime(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null; // Handle missing or null node
        }
        String dateStr = node.asText().trim();
        if (dateStr.isEmpty()) {
            return null; // Handle empty string
        }
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            // Handle parsing error, log it, or set a default value if needed
            System.err.println("DateTimeParseException: " + e.getMessage());
            return null; // Or handle the error as required
        }
    }

    public Coin findById(String coinId) throws Exception {
        return coinRepository.findById(coinId).orElseThrow(() -> new Exception("Couldn't find any coin with id " + coinId));
    }

    public String searchCoin(String keyword) throws Exception {
        String url = "https://api.coingecko.com/api/v3/search?query=" + keyword;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>( httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getTop50CoinByMarketCap() throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=50&page=1";
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>( httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getTrendingCoins() throws Exception {
        String url = "https://api.coingecko.com/api/v3/search/trending";
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }
}
