package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Dto.ResetPasswordRequest;
import com.trading.tradingbackend.Model.ForgotPassword;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.ForgotPasswordRepository;
import com.trading.tradingbackend.Repository.TokenRepository;
import com.trading.tradingbackend.Repository.UserRepository;
import com.trading.tradingbackend.Utils.OtpUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final EmailService emailService;
    private final UserDetailService userDetailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private ForgotPassword forgotPasswordTokenGenerator(User user){
        Optional<ForgotPassword> forgotPasswordToken=forgotPasswordRepository.findForgotPasswordTokenByUserEmail(user.getEmail());
        if(forgotPasswordToken.isPresent()){
            forgotPasswordRepository.delete(forgotPasswordToken.get());
        }
        return ForgotPassword.builder().otp(OtpUtils.generateOtp()).user(user).build();
    }

    public boolean sendForgotPasswordMail(User user) throws MessagingException {
        ForgotPassword forgotPasswordToken=forgotPasswordTokenGenerator(user);
        forgotPasswordRepository.save(forgotPasswordToken);
        try{
            emailService.sendPasswordRecoveryEmail(user.getEmail(),forgotPasswordToken.getOtp());
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    public boolean verifyForgotPasswordOtp(ResetPasswordRequest resetPasswordRequest) {
        Optional<ForgotPassword> forgotPasswordToken=forgotPasswordRepository.findForgotPasswordTokenByUserEmail(resetPasswordRequest.getEmail());
        if(forgotPasswordToken.isPresent() && forgotPasswordToken.get().getOtp().equals(resetPasswordRequest.getOtp())){
           try {
               User user = forgotPasswordToken.get().getUser();
               user.setPassword(new BCryptPasswordEncoder().encode(resetPasswordRequest.getNewPassword()));
               userRepository.save(user);
               forgotPasswordRepository.delete(forgotPasswordToken.get());
               return true;
           }
           catch (Exception e){
               return false;
           }
        }
        else{
            //Throw an exception or handle the error
            return false;
        }
    }
}
