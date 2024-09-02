package com.trading.tradingbackend.Repository;


import com.trading.tradingbackend.Model.WatchList;
import com.trading.tradingbackend.Model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList,Long> {

    @Query("select watchList from WatchList watchList where watchList.user.id=:userId")
    WatchList findAllByUserId(@Param("userId") Long userId);
}
