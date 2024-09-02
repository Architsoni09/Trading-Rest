package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Model.Wallet;
import com.trading.tradingbackend.Model.Withdrawal;
import com.trading.tradingbackend.Service.UserDetailService;
import com.trading.tradingbackend.Service.WalletService;
import com.trading.tradingbackend.Service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/withdrawal")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final JwtService jwtService;
    private final UserDetailService userService;
    private final WalletService walletService;

    @PostMapping("/{amount}")
    public ResponseEntity<Withdrawal> withdrawalRequest(@RequestHeader("Authorization") String jwt, @PathVariable Long amount) throws Exception{
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Wallet wallet=walletService.getUserWallet(user);
        Withdrawal withdrawal=withdrawalService.requestWithdrawal(amount,user);
        walletService.addBalance(wallet,-withdrawal.getAmount(),"Withdrawal Pending");
        return ResponseEntity.ok(withdrawal);
    }

    @PatchMapping("/admin/{withdrawalId}/proceed/{accept}")
    public ResponseEntity<Withdrawal> proceedWithdrawal(@RequestHeader("Authorization") String jwt,@PathVariable Long withdrawalId, @PathVariable boolean accept) throws Exception{
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Withdrawal withdrawal=withdrawalService.proceedWithWithdrawal(withdrawalId,accept);
        Wallet wallet=walletService.getUserWallet(user);
        if(!accept){
        walletService.addBalance(wallet,withdrawal.getAmount(),"Withdrawal Failed");
        }
        return ResponseEntity.ok(withdrawal);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawals(@RequestHeader("Authorization") String jwt){
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        return ResponseEntity.ok(withdrawalService.getUsersWithdrawalHistory(user));
    }


    @GetMapping("/admin/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalsByAdmin(@RequestHeader("Authorization") String jwt){
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        return ResponseEntity.ok(withdrawalService.getAllWithdrawalRequests());
    }


}
