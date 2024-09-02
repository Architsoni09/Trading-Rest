package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Enums.ORDER_STATUS;
import com.trading.tradingbackend.Enums.ORDER_TYPE;
import com.trading.tradingbackend.Model.*;
import com.trading.tradingbackend.Repository.AssetRepository;
import com.trading.tradingbackend.Repository.OrderItemRepository;
import com.trading.tradingbackend.Repository.OrderRepository;
import com.trading.tradingbackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final AssetService assetService;
    private final OrderRepository orderRepository;
    private final UserDetailService userDetailService;
    private final WalletService walletService;
    private final OrderItemRepository orderItemRepository;

    public Order saveOrder(User user, OrderItem orderItem, ORDER_TYPE orderType){
        return orderRepository.save(Order.builder()
                .user(user)
                .orderStatus(ORDER_STATUS.PENDING)
                .orderType(orderType)
                .price(BigDecimal.valueOf(orderItem.getCoin().getCurrentPrice().doubleValue()*orderItem.getQuantity()))
                .orderItem(orderItem)
                .timestamp(LocalDateTime.now())
                .build());
    }

    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId).orElseThrow(()-> new Exception("Order not found"));
    }

    public List<Order> getOrderListOfUser(Long userId){
        return orderRepository.findByUserIdAndOrderType(userId);
    }

    public OrderItem createOrderItem( Coin coin,double quantity,double cp,double sp) throws Exception{
        OrderItem orderItem = OrderItem.builder().coin(coin).quantity(quantity).costPrice(BigDecimal.valueOf(cp)).sellingPrice(BigDecimal.valueOf(sp)).build();
        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin,double quantity,User user) throws Exception {
        if(quantity<0){
            throw new Exception("Invalid quantity");
        }
        BigDecimal buyingPrice=coin.getCurrentPrice();
        OrderItem orderItem=createOrderItem(coin,quantity,buyingPrice.longValue(),0);
        Order order=saveOrder(user,orderItem,ORDER_TYPE.BUY);
        orderItem.setOrder(order);
        walletService.orderFinances(order,user);
        order.setOrderStatus(ORDER_STATUS.SUCCESS);
        order.setOrderType(ORDER_TYPE.BUY);
        Order savedOrder=orderRepository.save(order);
        //asset is pending
        Asset oldAsset=assetService.findAssetByUserIdAndCoinId(order.getUser().getId(), order.getOrderItem().getCoin().getId());
        if(oldAsset==null) {
            assetService.createAsset(user,orderItem.getCoin(),orderItem.getQuantity());
        }
        else{
            assetService.updateAsset(oldAsset.getId(),quantity);
        }
        return savedOrder;
    }

    @Transactional
    public Order sellAsset(Coin coin,double quantity,User user) throws Exception {
        if(quantity<0){
            throw new Exception("Invalid quantity");
        }
        BigDecimal sellingPrice=coin.getCurrentPrice();
        Asset assetToSell= assetService.findAssetByUserIdAndCoinId(user.getId(),coin.getId());
        if(assetToSell!=null) {
            BigDecimal buyingPrice = assetToSell.getCostPrice();
            OrderItem orderItem = createOrderItem(coin, quantity, buyingPrice.longValue(), sellingPrice.longValue());
            Order order = saveOrder(user, orderItem, ORDER_TYPE.SELL);
            orderItem.setOrder(order);
            if (assetToSell.getQuantity() > quantity) {
                walletService.orderFinances(order, user);
                order.setOrderStatus(ORDER_STATUS.SUCCESS);
                order.setOrderType(ORDER_TYPE.SELL);
                Order savedOrder = orderRepository.save(order);
                Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);
                BigDecimal quantityOfAsset = BigDecimal.valueOf(updatedAsset.getQuantity());
                BigDecimal totalValueOfAsset = quantityOfAsset.multiply(coin.getCurrentPrice());
                if (totalValueOfAsset.compareTo(BigDecimal.ONE) <= 0) {
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return savedOrder;
            }
            throw new Exception("Insufficient quantity to sell");
        }
        throw new Exception("No asset found for this user and coin");
    }

    @Transactional
    public Order processOrder(Coin coin,double quantity,ORDER_TYPE orderType,User user) throws Exception {
        if(orderType.equals( ORDER_TYPE.BUY)){
            return buyAsset(coin,quantity,user);
        } else if (orderType.equals(ORDER_TYPE.SELL)) {
            return sellAsset(coin,quantity,user);
        }
        throw new Exception("Invalid order type");

    }

}
