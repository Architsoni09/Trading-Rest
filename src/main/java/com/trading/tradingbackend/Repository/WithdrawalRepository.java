package com.trading.tradingbackend.Repository;


import com.trading.tradingbackend.Model.Asset;
import com.trading.tradingbackend.Model.Token;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal,Long> {

    @Query("select withdrawal from Withdrawal withdrawal where withdrawal.user.id=:userId order by withdrawal.id desc ")
    List<Withdrawal> findAllByUserId(@Param("userId") Long userId);

    @Query("select withdrawal.user from Withdrawal withdrawal where withdrawal.id=:withdrawalId order by withdrawal.id desc")
    Optional<User> findUserByWithdrawalId(@Param("withdrawalId") Long withdrawalId);
}
