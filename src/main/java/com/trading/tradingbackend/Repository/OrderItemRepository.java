package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.Coin;
import com.trading.tradingbackend.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
