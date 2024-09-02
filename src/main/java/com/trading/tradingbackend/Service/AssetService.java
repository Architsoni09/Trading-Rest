package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Model.Asset;
import com.trading.tradingbackend.Model.Coin;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    public Asset createAsset(User user, Coin coin,double quantity){
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setCostPrice(coin.getCurrentPrice());
        return assetRepository.save(asset);
    }

    public Asset getAssetById(Long assetId) throws Exception {
        return assetRepository.findById(assetId).orElseThrow(()->new Exception("Asset not found for assetId: " + assetId));
    }

//    public Asset getAssetByUserIdAndAssetId(Long userId,Long assetId){
//
//        return null;
////        return assetRepository.save(Asset.builder().user().build());
//    }

    public List<Asset> getAllUserAssets(Long userId){
        return assetRepository.findAllByUserId(userId);
    }

    public Asset updateAsset(Long assetId,double quantity) throws Exception {
        Asset asset = getAssetById(assetId);
        asset.setQuantity(quantity+asset.getQuantity());
        return assetRepository.save(asset);
    }

    public Asset findAssetByUserIdAndCoinId(Long userId,String coinId){
        return assetRepository.findByUserIdAndCoinId(userId, coinId);
    }
    public void deleteAsset(Long assetId){
        assetRepository.deleteById(assetId);
    }

}
