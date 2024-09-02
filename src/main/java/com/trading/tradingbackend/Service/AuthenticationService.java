package com.trading.tradingbackend.Service;
import ch.qos.logback.core.util.StringUtil;
import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.AuthenticationRequest;
import com.trading.tradingbackend.Dto.AuthenticationResponse;
import com.trading.tradingbackend.Dto.RegistrationRequest;
import com.trading.tradingbackend.Enums.VERIFICATION_TYPE;
import com.trading.tradingbackend.Exceptions.AuthenticationFailedException;
import com.trading.tradingbackend.Exceptions.UserAlreadyExistsException;
import com.trading.tradingbackend.Model.*;
import com.trading.tradingbackend.Repository.RoleRepository;
import com.trading.tradingbackend.Repository.TokenRepository;
import com.trading.tradingbackend.Repository.UserRepository;
import com.trading.tradingbackend.Utils.OtpUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
//    @Value("${application.security.mailing.frontend.activation-url}")
//    private String activationUrl;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TwoFactorOTPService otpService;
    private final EmailService emailService;
    private final TwoFactorOTPService twoFactorOTPService;


     public User register(RegistrationRequest request)  throws Exception {
        Role role= roleRepository.findByRoleName("USER").orElseThrow(()->new RuntimeException("Role not found"));
        Optional<User> existingUser =userRepository.findUserByEmail(request.getEmail());
        if(existingUser.isEmpty()) {
            String activationCode=generateActivationCode(6);
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUserName())
                    .mobileNumber(request.getMobileNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountLocked(false)
                    .twoFactorAuth(TwoFactorAuth.builder().twoFactorEnabled(request.getIsTwoFactorEnabled()).verificationType(VERIFICATION_TYPE.email).build())
                    .isActivated(false)
                    .activationCode(activationCode)
                    .roles(List.of(role))
                    .build();
            User savedUser= userRepository.save(user);
            emailService.sendActivationEmail(user.getEmail(),activationCode);
            return savedUser;
        }
        else if(!existingUser.get().isActivated()){
            userRepository.delete(existingUser.get());
            String activationCode=generateActivationCode(6);
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUserName())
                    .mobileNumber(request.getMobileNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountLocked(false)
                    .twoFactorAuth(TwoFactorAuth.builder().twoFactorEnabled(request.getIsTwoFactorEnabled()).verificationType(VERIFICATION_TYPE.email).build())
                    .isActivated(false)
                    .activationCode(activationCode)
                    .roles(List.of(role))
                    .build();
            User savedUser= userRepository.save(user);
            emailService.sendActivationEmail(user.getEmail(),activationCode);
            return savedUser;
        }
        else {
            throw new UserAlreadyExistsException("User already exists with this email");
        }
    }


    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }


    public AuthenticationResponse login(AuthenticationRequest request) throws MessagingException {
       try{
       var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
           claims.put("email",user.getEmail());

        if (!user.isActivated()) {
            return AuthenticationResponse.builder()
                    .message("Activate your Account First then Login")
                    .build();
        }

        Token token = jwtService.generateToken(claims, user);
        if (user.getTwoFactorAuth().getTwoFactorEnabled()) {
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .jwtToken(token.getToken())
                    .message("Two Factor Authentication is Enabled Kindly Enter The otp sent to your mail")
                    .isTwoFactorAuthEnabled(true)
                    .status(false)
                    .build();

            user.getTwoFactorAuth().setIsUserVerified(false);
            userRepository.save(user);
            String otp = OtpUtils.generateOtp();
            TwoFactorOTP oldOtp = otpService.getOtpByUserId(user.getId());
            if (oldOtp != null) {
                otpService.deleteOtp(oldOtp);
            }

            TwoFactorOTP newOtp = otpService.createTwoFactorOtp(user, otp, token);
            emailService.sendVerificationEmail(user.getEmail(), newOtp.getOtp());
            response.setSession(newOtp.getId());
            return response;
        }

        return AuthenticationResponse.builder()
                .jwtToken(token.getToken())
                .message("Login Successful!")
                .build();
       }
       catch (Exception e){
           return AuthenticationResponse.builder().status(false).message("Invalid Login Credentials").build();
       }
    }


    public boolean activateAccount(String email,String activationCode) throws Exception {
            User user=userRepository.findUserByEmail(email).orElseThrow(()->new UsernameNotFoundException(" user not found"));
            if(!user.isActivated()&& user.getActivationCode().equals(activationCode)){
                user.setActivated(true);
                userRepository.save(user);
                return true;
            }
            return user.isActivated();
    }

    public ResponseEntity<AuthenticationResponse> verifyOtp(String otp, String otpId) {
        // Fetch the OTP object using the provided OTP ID
        TwoFactorOTP otpObj = twoFactorOTPService.getOtpById(otpId);

        // Check if OTP object is null
        if (otpObj == null) {
            return ResponseEntity.badRequest()
                    .body(AuthenticationResponse.builder()
                            .message("Invalid OTP/ OTP Id")
                            .status(false)
                            .build());
        }

        // Verify the provided OTP against the OTP object
        boolean isOtpValid = twoFactorOTPService.verifyTwoFactorOtp(otp, otpObj);

        if (isOtpValid) {
            // OTP is valid, you can proceed with further actions here, e.g., logging in the user

            // Optionally delete the OTP after successful verification
            twoFactorOTPService.deleteOtp(otpObj);
            User user=otpObj.getUser();
            user.getTwoFactorAuth().setIsUserVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok()
                    .body(AuthenticationResponse.builder()
                            .message("OTP verified successfully")
                            .status(true)
                            .build());
        } else {
            // OTP is invalid
            return ResponseEntity.badRequest()
                    .body(AuthenticationResponse.builder()
                            .message("Invalid OTP")
                            .status(false)
                            .build());
        }
    }
}
