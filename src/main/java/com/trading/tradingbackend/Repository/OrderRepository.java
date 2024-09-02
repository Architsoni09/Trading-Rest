package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("select order from Order order where order.user.id=:userId")
    List<Order> findByUserIdAndOrderType(@Param("userId")Long userId);

}
