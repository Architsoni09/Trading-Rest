package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorOTPRepository extends JpaRepository<TwoFactorOTP,String> {
    @Query("select otp from TwoFactorOTP otp where otp.user.id=:userId")
    Optional<TwoFactorOTP> findByUserId(@Param("userId")Long userId);
}
