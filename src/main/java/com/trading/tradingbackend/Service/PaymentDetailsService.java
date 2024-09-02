package com.trading.tradingbackend.Service;

import com.razorpay.Payment;
import com.trading.tradingbackend.Model.PaymentDetails;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.PaymentDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentDetailsService {
    private final PaymentDetailsRepository paymentDetailsRepository;

    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifscCode, String bankName, User user){
        PaymentDetails paymentDetails=new PaymentDetails();
        paymentDetails.setAccountNumber(accountNumber);
        paymentDetails.setAccountHolderName(accountHolderName);
        paymentDetails.setIfscCode(ifscCode);
        paymentDetails.setBankName(bankName);
        paymentDetails.setUser(user);
        return paymentDetailsRepository.save(paymentDetails);
    }

    public PaymentDetails getUsersPaymentDetails(User user){
        return paymentDetailsRepository.findByUserId(user.getId()).orElse(null);
    }
}
