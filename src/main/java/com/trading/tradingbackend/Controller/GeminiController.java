package com.trading.tradingbackend.Controller;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.GeminiPromptRequest;
import com.trading.tradingbackend.Dto.GeminiPromptResponse;
import com.trading.tradingbackend.Dto.PaymentResponse;
import com.trading.tradingbackend.Enums.PAYMENT_METHOD;
import com.trading.tradingbackend.Exceptions.UserAlreadyExistsException;
import com.trading.tradingbackend.Model.PaymentOrder;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Service.AiService;
import com.trading.tradingbackend.Service.PaymentOrderService;
import com.trading.tradingbackend.Service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {
    private final UserDetailService userService;
    private final JwtService jwtService;
    private final AiService aiService;

    @PostMapping("/prompt")
    public ResponseEntity<GeminiPromptResponse> promptHandler(@RequestHeader("Authorization") String jwt, @RequestBody GeminiPromptRequest request) throws UserAlreadyExistsException, RazorpayException, StripeException {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        if(user!=null){
            GeminiPromptResponse response=aiService.promptHandler(request.getPrompt());
            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

}
