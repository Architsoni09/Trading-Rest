package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.ForgotPassword;
import com.trading.tradingbackend.Model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {

    @Query("SELECT fp FROM ForgotPassword fp where fp.user.email=:email")
    Optional<PaymentOrder> findForgotPasswordTokenByUserEmail(@Param("email") String email);
}
