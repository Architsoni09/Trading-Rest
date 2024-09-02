package com.trading.tradingbackend.Repository;
import com.trading.tradingbackend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT user FROM User user where user.email=:email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT user FROM User user where user.email=:username")
    Optional<User> findUserByUsername(@Param("username") String username);
}
