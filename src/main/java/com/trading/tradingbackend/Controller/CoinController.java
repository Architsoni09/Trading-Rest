package com.trading.tradingbackend.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.tradingbackend.Model.Coin;
import com.trading.tradingbackend.Service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/coins")
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;
    private final ObjectMapper objectMapper;
    @GetMapping("/list")
    public ResponseEntity<List<Coin>> getCoinList(@RequestParam(name="page",required = false,defaultValue = "1") int page) {
        try {
            return ResponseEntity.ok(coinService.getCoinList(page));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/market-chart")
    public ResponseEntity<JsonNode> getMarketChart(@RequestParam(name = "coin_id") String coinId, @RequestParam(name = "days") int days) {
        try {
            String response=coinService.getMarketChart(coinId, days);
            JsonNode responseNode=objectMapper.readTree(response);
            return ResponseEntity.ok(responseNode);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/details/{coinId}")
    public ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) {
        try {
            String response = coinService.getCoinDetails(coinId);
            JsonNode responseNode = objectMapper.readTree(response);
            return ResponseEntity.ok(responseNode);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{coinId}")
    public ResponseEntity<JsonNode> findById(@PathVariable String coinId) {
        try {
            Coin coin = coinService.findById(coinId);
            JsonNode responseNode = objectMapper.valueToTree(coin);
            return ResponseEntity.ok(responseNode);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }


    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) {
        try {
            String response =coinService.searchCoin(keyword);
            JsonNode responseNode = objectMapper.readTree(response);
            return ResponseEntity.ok(responseNode);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/top-50")
    public ResponseEntity<JsonNode> getTop50CoinByMarketCap() {
        try {
            String response =coinService.getTop50CoinByMarketCap();
            JsonNode responseNode = objectMapper.readTree(response);
            return ResponseEntity.ok(responseNode);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/trending-coins")
    public ResponseEntity<JsonNode> getTrendingCoins() {
        try {
            String response =coinService.getTrendingCoins();
            JsonNode responseNode = objectMapper.readTree(response);
            return ResponseEntity.ok(responseNode);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
