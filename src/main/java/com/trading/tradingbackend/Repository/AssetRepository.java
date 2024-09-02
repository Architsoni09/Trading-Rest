package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.Asset;
import com.trading.tradingbackend.Model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset,Long> {

    @Query("select asset from Asset asset where asset.user.id=:userId")
    List<Asset> findAllByUserId(@Param("userId") Long userId);

    @Query("select asset from Asset asset where asset.user.id=:userId and asset.coin.id=:coinId")
    Asset findByUserIdAndCoinId(@Param("userId") Long userId ,@Param("coinId") String coinId);

}
