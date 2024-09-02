package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Model.Asset;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Service.AssetService;
import com.trading.tradingbackend.Service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/asset")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;
    private final UserDetailService userService;
    private final JwtService jwtService;


    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetByAssetId( @PathVariable Long assetId) throws Exception {
        Asset asset=assetService.getAssetById(assetId);
        return new ResponseEntity<>(asset, HttpStatus.OK);
    }

    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<Asset> getAssetByCoinIdAndUserId( @PathVariable String coinId, @RequestHeader("Authorization") String jwt) throws Exception {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Long userId = user.getId();
        Asset asset=assetService.findAssetByUserIdAndCoinId(userId,coinId);
        return new ResponseEntity<>(asset, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Asset>> getAllAssetsForUser(@RequestHeader("Authorization") String jwt) throws Exception {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Long userId = user.getId();
        List<Asset> assets=assetService.getAllUserAssets(userId);
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }
}
