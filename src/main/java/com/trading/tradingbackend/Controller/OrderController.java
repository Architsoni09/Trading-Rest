package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.CreateOrderRequest;
import com.trading.tradingbackend.Enums.ORDER_TYPE;
import com.trading.tradingbackend.Model.Coin;
import com.trading.tradingbackend.Model.Order;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Model.WalletTransaction;
import com.trading.tradingbackend.Service.CoinService;
import com.trading.tradingbackend.Service.OrderService;
import com.trading.tradingbackend.Service.UserDetailService;
import com.trading.tradingbackend.Service.WalletService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserDetailService userService;
    private final CoinService coinService;
    private final WalletService walletService;
    private final JwtService jwtService;


    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(@RequestHeader("Authorization") String jwt, @RequestBody CreateOrderRequest request) {
        try {
            String username = jwtService.extractUserNameFromToken(jwt);
            User user = (User) userService.loadUserByUsername(username);
            Coin coin = coinService.findById(request.getCoinId());

            Order order = orderService.processOrder(coin, request.getQuantity(), request.getOrderType(), user);

            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<Order> findById(@RequestHeader("Authorization") String jwt,@PathVariable Long orderId) throws Exception {
        if(jwt==null) throw new IllegalArgumentException("Token is required for you to proceed");
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Order order= orderService.getOrderById(orderId);
        if(order.getUser().getId().equals(user.getId())){
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
        else{
            throw new Exception("You don't have access");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrdersForUser(@RequestHeader("Authorization") String jwt
                                                           ) throws Exception{
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userService.loadUserByUsername(username);
        Long userId = user.getId();

        List<Order> orders = orderService.getOrderListOfUser(userId);
        return ResponseEntity.ok(orders);
    }
}
