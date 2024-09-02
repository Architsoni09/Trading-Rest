package com.trading.tradingbackend.Controller;
import com.trading.tradingbackend.Config.JwtService;
import com.trading.tradingbackend.Dto.AuthenticationRequest;
import com.trading.tradingbackend.Dto.AuthenticationResponse;
import com.trading.tradingbackend.Dto.ProfileDetailsUpdateRequest;
import com.trading.tradingbackend.Dto.RegistrationRequest;
import com.trading.tradingbackend.Exceptions.UserAlreadyExistsException;
import com.trading.tradingbackend.Model.TwoFactorOTP;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Service.AuthenticationService;
import com.trading.tradingbackend.Service.TwoFactorOTPService;
import com.trading.tradingbackend.Service.UserDetailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserDetailService userService;
    private final TwoFactorOTPService twoFactorOTPService;
    private final JwtService jwtService;



    @GetMapping("/welcome")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Welcome to the world!");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws Exception {
        try {
            User savedUser=authenticationService.register(request);
            return new ResponseEntity<>(savedUser,HttpStatus.ACCEPTED);
        }
        catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) throws MessagingException {
        try {
            AuthenticationResponse response = authenticationService.login(request);
            if(response.getMessage().equals("Invalid Login Credentials")||response.getMessage().equals("Activate your Account First then Login")){
                throw new Exception(response.getMessage());
            }
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/verify-otp/{otp}/{otpId}")
    public ResponseEntity<AuthenticationResponse> verifyOtp(@PathVariable String otp, @PathVariable String otpId) {
        return authenticationService.verifyOtp(otp, otpId);
    }

    @PostMapping("/activate-account/{email}/{activationCode}")
    public boolean activateAccount(@PathVariable String email,@PathVariable String activationCode) throws Exception {
       return authenticationService.activateAccount(email,activationCode);
    }


}
