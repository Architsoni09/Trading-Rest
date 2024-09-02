package com.trading.tradingbackend.Repository;

import com.trading.tradingbackend.Model.ForgotPassword;
import com.trading.tradingbackend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword,String> {

    @Query("SELECT fp FROM ForgotPassword fp where fp.user.email=:email")
    Optional<ForgotPassword> findForgotPasswordTokenByUserEmail(@Param("email") String email);
}
