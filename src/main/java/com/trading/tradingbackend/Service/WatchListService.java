package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Model.Coin;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Model.WatchList;
import com.trading.tradingbackend.Repository.WatchListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchListService {
    private final UserDetailService userDetailService;
    private final WatchListRepository watchListRepository;

    public WatchList findUserWatchList(User user) throws Exception {
        WatchList watchList=watchListRepository.findAllByUserId(user.getId());
        if(watchList==null) watchList=createWatchList(user);
        return watchList;
    }

    public WatchList createWatchList(User user) {
        WatchList watchList=new WatchList();
        watchList.setUser(user);
        return watchListRepository.save(watchList);
    }

    public WatchList findWatchListById(Long watchListId){
        return watchListRepository.findById(watchListId).orElseThrow(() -> new RuntimeException("No watchList found for the given id"));
    }
    public Coin addItemToWatchList(Coin coin,User user) throws Exception {
        WatchList watchList=findUserWatchList(user);
        if(watchList.getCoins().contains(coin)){
            watchList.getCoins().remove(coin);
        }
        else {
            watchList.getCoins().add(coin);
        }
        watchListRepository.save(watchList);
        return coin;
    }
}
