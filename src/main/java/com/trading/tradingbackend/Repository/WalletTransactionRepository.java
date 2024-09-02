package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.Wallet;
import com.trading.tradingbackend.Model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {
    @Query("select transaction from WalletTransaction transaction where transaction.wallet.user.id=:id order by transaction.id desc ")
    Optional<List<WalletTransaction>> findTransactionsByUserId(@Param("id") Long id);
}
