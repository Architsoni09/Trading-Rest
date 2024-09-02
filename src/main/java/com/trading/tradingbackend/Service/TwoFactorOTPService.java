package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Model.Token;
import com.trading.tradingbackend.Model.TwoFactorOTP;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.TwoFactorOTPRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFactorOTPService {
    private final TwoFactorOTPRepository repository;

     TwoFactorOTP createTwoFactorOtp(User user, String otp, Token token){
        UUID uuid=UUID.randomUUID();
        String id=uuid.toString();
        TwoFactorOTP twoFactorOTP=new TwoFactorOTP();
        twoFactorOTP.setId(id);
        twoFactorOTP.setJwt(token);
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setUser(user);
        return repository.save(twoFactorOTP);
    }
    TwoFactorOTP getOtpByUserId(Long userId){
        return repository.findByUserId(userId).orElse(null);
    }
     public TwoFactorOTP getOtpById(String otpId){
        return repository.findById(otpId).orElse(null);
    }
     public boolean verifyTwoFactorOtp(String otp, TwoFactorOTP twoFactorOTP){
        return twoFactorOTP.getOtp().equals(otp);
    }
    public void deleteOtp(TwoFactorOTP twoFactorOTP){
        repository.delete(twoFactorOTP);
    }
}
