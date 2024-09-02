package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.PaymentDetailsRequest;
import com.trading.tradingbackend.Model.PaymentDetails;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Service.AssetService;
import com.trading.tradingbackend.Service.PaymentDetailsService;
import com.trading.tradingbackend.Service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
public class PaymentDetailsController {
    private final UserDetailService userService;
    private final JwtService jwtService;
    private final PaymentDetailsService paymentDetailsService;

    @PostMapping("add/payment-details")
    public ResponseEntity<PaymentDetails> addPaymentDetails(@RequestBody PaymentDetailsRequest paymentDetailRequest, @RequestHeader("Authorization") String jwt) throws Exception{
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
       PaymentDetails paymentDetails= paymentDetailsService.addPaymentDetails(
                paymentDetailRequest.getAccountNumber(),
                paymentDetailRequest.getAccountHolderName(),
                paymentDetailRequest.getIfscCode(),
                paymentDetailRequest.getBankName(),user
        );
        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping("/user/payment-details")
    public ResponseEntity<PaymentDetails> getUserPaymentDetails( @RequestHeader("Authorization") String jwt) throws Exception {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        PaymentDetails paymentDetails = paymentDetailsService.getUsersPaymentDetails(user);
        if(paymentDetails==null) return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }

}
