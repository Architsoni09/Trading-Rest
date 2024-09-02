package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Dto.ProfileDetailsUpdateRequest;
import com.trading.tradingbackend.Enums.VERIFICATION_TYPE;
import com.trading.tradingbackend.Model.TwoFactorAuth;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(()->new UsernameNotFoundException("Username not found"));
    }

    public User enableTwoFactorAuthentication(User user) {
       TwoFactorAuth twoFactorAuth= user.getTwoFactorAuth();
       twoFactorAuth.setTwoFactorEnabled(true);
        twoFactorAuth.setVerificationType(VERIFICATION_TYPE.email);
        twoFactorAuth.setIsUserVerified(true);
        user.setTwoFactorAuth(twoFactorAuth);
        return userRepository.save(user);
    }

    public User disableTwoFactorAuthentication(User user) {
        TwoFactorAuth twoFactorAuth= user.getTwoFactorAuth();
        twoFactorAuth.setTwoFactorEnabled(false);
        twoFactorAuth.setVerificationType(VERIFICATION_TYPE.email);
        twoFactorAuth.setIsUserVerified(true);
        user.setTwoFactorAuth(twoFactorAuth);
        return userRepository.save(user);
    }

    public User updateProfileDetails(ProfileDetailsUpdateRequest request, User user) {

        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPinCode(request.getPinCode());
        user.setNationality(request.getNationality());
        user.setCountry(request.getCountry());
        user.setDateOfBirth(request.getDateOfBirth());
        return userRepository.save(user);
    }

}
