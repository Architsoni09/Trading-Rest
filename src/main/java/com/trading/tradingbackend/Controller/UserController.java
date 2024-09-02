package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.ProfileDetailsUpdateRequest;
import com.trading.tradingbackend.Dto.ResetPasswordRequest;
import com.trading.tradingbackend.Dto.ResetPasswordResponse;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.UserRepository;
import com.trading.tradingbackend.Service.EmailService;
import com.trading.tradingbackend.Service.ForgotPasswordService;
import com.trading.tradingbackend.Service.UserDetailService;
import com.trading.tradingbackend.Utils.OtpUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailService userDetailService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;


    @GetMapping("/profile")
    public ResponseEntity<User> getUserDetailsByJwt(@RequestHeader("Authorization") String jwt){
        String username = jwtService.extractUserNameFromToken(jwt);
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PatchMapping("/enable-two-factor-authentication")
    public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt){
        String username = jwtService.extractUserNameFromToken(jwt);
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isPresent()) {
            return ResponseEntity.ok(userDetailService.enableTwoFactorAuthentication(user.get()));
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/disable-two-factor-authentication")
    public ResponseEntity<User> disableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt){
        String username = jwtService.extractUserNameFromToken(jwt);
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isPresent()) {
            return ResponseEntity.ok(userDetailService.disableTwoFactorAuthentication(user.get()));
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<String> registerUser(@PathVariable String email) throws MessagingException {
        Optional<User> user = userRepository.findUserByEmail(email);
        if(user.isPresent()) {
            // Send email with password reset link
            boolean isSuccessful=forgotPasswordService.sendForgotPasswordMail(user.get());
            return isSuccessful?ResponseEntity.ok("Email Successfully Sent")
                    :ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Oops Something went wrong , please try again.");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Oops The Email Id is not registered , please try again.");
        }
    }
    @PostMapping("/forgot-password/otp-verification")
    public ResponseEntity<ResetPasswordResponse> verifyOtp(@RequestBody ResetPasswordRequest resetPasswordRequest){
        Optional<User> user = userRepository.findUserByEmail(resetPasswordRequest.getEmail());
        if(user.isPresent()) {
            boolean isSuccessful=forgotPasswordService.verifyForgotPasswordOtp(resetPasswordRequest);
            return isSuccessful?ResponseEntity.ok(ResetPasswordResponse.builder().success(true).message("Otp is verified successfully").build()):
                    ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ResetPasswordResponse.builder().success(false).message("Oops Something went wrong, please Try again.").build());
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResetPasswordResponse.builder().success(false).message("email Id is not registered ").build());
        }
    }

    @PutMapping("/update-profile-details")
    public ResponseEntity<User> updateProfileDetails(@RequestBody ProfileDetailsUpdateRequest request, @RequestHeader("Authorization") String jwt) throws Exception {
        String username = jwtService.extractUserNameFromToken(jwt);
        User user = (User) userDetailService.loadUserByUsername(username);
        User response=userDetailService.updateProfileDetails(request,user);
        if(response!=null){
            return ResponseEntity.ok(response);
        }
        else{
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }
}
