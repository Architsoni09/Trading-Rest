package com.trading.tradingbackend;

import com.trading.tradingbackend.Model.Role;
import com.trading.tradingbackend.Repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TradingbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingbackendApplication.class, args);
    }
    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository){
        return args -> {
            if(roleRepository.findByRoleName("USER").isEmpty()){
                roleRepository.save(Role.builder().roleName("USER").build());
            }
        };
    }
}
