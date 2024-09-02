package com.trading.tradingbackend.Controller;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.PaymentResponse;
import com.trading.tradingbackend.Enums.PAYMENT_METHOD;
import com.trading.tradingbackend.Exceptions.UserAlreadyExistsException;
import com.trading.tradingbackend.Model.PaymentOrder;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Service.PaymentOrderService;
import com.trading.tradingbackend.Service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final UserDetailService userService;
    private final JwtService jwtService;
    private final PaymentOrderService paymentOrderService;

    @PostMapping("/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(@RequestHeader("Authorization") String jwt, @PathVariable Long amount, @PathVariable PAYMENT_METHOD paymentMethod) throws UserAlreadyExistsException, RazorpayException, StripeException {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);

        PaymentResponse paymentResponse;

        PaymentOrder paymentOrder=paymentOrderService.createOrder(user,amount,paymentMethod);
        if(paymentMethod.equals(PAYMENT_METHOD.RAZORPAY)){
            paymentResponse=paymentOrderService.createRazorPayPaymentLink(user,amount,paymentOrder.getId());
        }
        else if(paymentMethod.equals(PAYMENT_METHOD.STRIPE)){
            paymentResponse=paymentOrderService.createStripePaymentLink(user,amount,paymentOrder.getId());
        }
        else{
            paymentResponse=paymentOrderService.addMoneyForFree(user,amount,paymentOrder.getId());
        }
        paymentResponse.setPaymentId(paymentOrder.getId());
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

}
