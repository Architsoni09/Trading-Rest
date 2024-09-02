package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.Asset;
import com.trading.tradingbackend.Model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Long> {

    @Query("select payment from PaymentDetails payment where payment.user.id=:userId")
    Optional<PaymentDetails> findByUserId(@Param("userId") Long userId);

}
