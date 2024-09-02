package com.trading.tradingbackend.Service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trading.tradingbackend.Dto.PaymentResponse;
import com.trading.tradingbackend.Enums.PAYMENT_METHOD;
import com.trading.tradingbackend.Enums.PAYMENT_ORDER_STATUS;
import com.trading.tradingbackend.Model.PaymentOrder;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentOrderService {
    private final PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String razorPayApiKey;

    @Value("${razorpay.api.secret}")
    private String razorPayApiSecretKey;


    public PaymentOrder createOrder(User user, Long amount, PAYMENT_METHOD paymentMethod) {
        PaymentOrder paymentOrder = PaymentOrder
                .builder()
                .paymentMethod(paymentMethod)
                .amount(amount)
                .paymentStatus(PAYMENT_ORDER_STATUS.PENDING)
                .user(user)
                .build();
        return paymentOrderRepository.save(paymentOrder);
    }

    public PaymentOrder getPaymentOrderById(Long orderId) throws Exception {
        return paymentOrderRepository.findById(orderId).orElseThrow(() -> new Exception("Could not find payment order."));
    }

    public boolean proceedWithPaymentOrder(PaymentOrder order, String paymentId) throws RazorpayException {
        if (order.getPaymentStatus()==null) {order.setPaymentStatus(PAYMENT_ORDER_STATUS.PENDING);}
        if (order.getPaymentStatus().equals(PAYMENT_ORDER_STATUS.PENDING)) {
            if (order.getPaymentMethod().equals(PAYMENT_METHOD.RAZORPAY)) {
                RazorpayClient razorpayClient = new RazorpayClient(razorPayApiKey, razorPayApiSecretKey);
                Payment payment = razorpayClient.payments.fetch(paymentId);
                Integer amount = payment.get("amount");
                String status = payment.get("status");
                if (status.equals("captured")) {
                    order.setPaymentStatus(PAYMENT_ORDER_STATUS.SUCCESS);
                    return true;
                }
                order.setPaymentStatus(PAYMENT_ORDER_STATUS.FAILED);
                paymentOrderRepository.save(order);
                return false;
            }
            order.setPaymentStatus(PAYMENT_ORDER_STATUS.SUCCESS);
            paymentOrderRepository.save(order);
            return true;
        }
        return false;
    }

    public PaymentResponse createRazorPayPaymentLink(User user, Long amount,Long orderId) throws RazorpayException {
        Long Amount = amount * 100;
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorPayApiKey, razorPayApiSecretKey);
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", Amount);
            paymentLinkRequest.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", user.getUsername());
            customer.put("email", user.getEmail());
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);
            paymentLinkRequest.put("reminder_enable", true);

            paymentLinkRequest.put("callback_url", "http://localhost:4200/wallet?order_id="+orderId);
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);
            String paymentLinkId = paymentLink.get("id");
            String paymentLinkUrl = paymentLink.get("short_url");

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setPaymentUrl(paymentLinkUrl);
            return paymentResponse;
        } catch (RazorpayException e) {
            System.out.println("Error in creating payment Link: " + e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
    }

    public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        SessionCreateParams params = SessionCreateParams
                .builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/wallet?order_id=" + orderId)
                .setCancelUrl("http://localhost:4200/payment/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setQuantity(1L).setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amount * 100)
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName("Top up Wallet")
                                                        .build())
                                                .build())
                                .build()
                )
                .build();

        Session session=Session.create(params);
        System.out.println("session ______"+session);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentUrl(session.getUrl());
        return paymentResponse;
    }


    public PaymentResponse addMoneyForFree(User user, Long amount, Long orderId) {
        // Example open API URL for testing purposes
        String openApiUrl = "https://jsonplaceholder.typicode.com/posts/1";

        // Assuming you would want to pass some parameters along with the URL
        String finalUrl = openApiUrl + "?userId=" + user.getId() + "&orderId=" + orderId;

        // Returning the PaymentResponse with the open API URL
        return PaymentResponse.builder().paymentUrl(finalUrl).build();
    }

}
