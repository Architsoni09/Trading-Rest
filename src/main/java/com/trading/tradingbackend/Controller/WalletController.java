package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.WalletTransactionRequest;
import com.trading.tradingbackend.Model.*;
import com.trading.tradingbackend.Service.OrderService;
import com.trading.tradingbackend.Service.PaymentOrderService;
import com.trading.tradingbackend.Service.UserDetailService;
import com.trading.tradingbackend.Service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserDetailService userService;
    private final JwtService jwtService;
    private final OrderService orderservice;
    private final PaymentOrderService paymentOrderService;
    private final RestTemplate restTemplate;

    @GetMapping("/get-user-wallet")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        return ResponseEntity.ok(walletService.getUserWallet(user));
    }

    @PutMapping("/wallet-transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization") String jwt,
                                                         @RequestBody WalletTransactionRequest request) {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Wallet recieverWallet = walletService.findWalletById(request.getReceiverWalletId());
        return ResponseEntity.ok(walletService.walletToWalletTransfer(user, recieverWallet, request.getAmount(),request.getPurpose()));
    }

    @PutMapping("/order/{orderId}/pay")
    public ResponseEntity<Wallet> orderPayment(@RequestHeader("Authorization") String jwt,
                                               @PathVariable Long orderId) throws Exception {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Order order= orderservice.getOrderById(orderId);
        Wallet recieverWallet = walletService.orderFinances(order,user);
        return ResponseEntity.ok(recieverWallet);
    }

    @PutMapping("/deposit")
    public ResponseEntity<Wallet> addMoneyToWallet(@RequestHeader("Authorization") String jwt,
                                               @RequestParam(name = "payment_id") String paymentId) throws Exception {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);

        Wallet recieverWallet = walletService.getUserWallet(user);
        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderById(Long.parseLong(paymentId));
        boolean status=paymentOrderService.proceedWithPaymentOrder(paymentOrder,paymentId);

        if(status){
            recieverWallet=walletService.addBalance(recieverWallet,paymentOrder.getAmount(),"Top Up");
        }
        return ResponseEntity.ok(recieverWallet);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@RequestHeader("Authorization") String jwt) {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        return ResponseEntity.ok(walletService.getTransactions(user));
    }



}
