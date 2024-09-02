package com.trading.tradingbackend.Controller;

import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.UserRepository;
import com.trading.tradingbackend.Service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class HomeController {
    private final UserDetailService service;

    @GetMapping("/getUserByEmail")
    public void getUserByEmail(@RequestBody String email) {
        User user = (User) service.loadUserByUsername(email);
        System.out.println(user.toString());
    }

    @GetMapping("/dummy")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Hey Friend", HttpStatus.OK);
    }
}
