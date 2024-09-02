package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {
    @Query("select wallet from Wallet wallet where wallet.user.id=:id")
    Optional<Wallet> findByUserId(@Param("id") Long id);
}
