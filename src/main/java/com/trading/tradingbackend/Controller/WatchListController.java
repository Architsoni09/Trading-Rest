package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Model.Coin;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Model.WatchList;
import com.trading.tradingbackend.Service.CoinService;
import com.trading.tradingbackend.Service.UserDetailService;
import com.trading.tradingbackend.Service.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchListController {
    private final JwtService jwtService;
    private final WatchListService watchListService;
    private final CoinService coinService;
    private final UserDetailService userService;

    @GetMapping("/user")
    public ResponseEntity<WatchList> getUserWatchList(@RequestHeader("Authorization") String jwt) throws Exception{
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        WatchList watchList= watchListService.findUserWatchList(user);
        return ResponseEntity.ok(watchList);
    }

    @PostMapping("/create")
    public ResponseEntity<WatchList> createWatchList(@RequestHeader("Authorization") String jwt) throws Exception{
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        WatchList watchList = watchListService.createWatchList(user);
        return new ResponseEntity<>(watchList, HttpStatus.CREATED);
    }

    @GetMapping("/{watchListId}")
    public ResponseEntity<WatchList> getWatchListById(@PathVariable Long watchListId) throws Exception{
        WatchList watchList = watchListService.findWatchListById(watchListId);
        return ResponseEntity.ok(watchList);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addCoinToWatchList(@RequestHeader("Authorization") String jwt, @PathVariable String coinId) throws Exception{
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Coin coin=coinService.findById(coinId);
        Coin addedCoin=watchListService.addItemToWatchList(coin,user);
        return ResponseEntity.ok(addedCoin);
    }
}
