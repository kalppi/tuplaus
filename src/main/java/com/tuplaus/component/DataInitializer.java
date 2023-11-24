package com.tuplaus.component;

import com.tuplaus.entity.User;
import com.tuplaus.repository.UserRepository;
import com.tuplaus.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public void run(ApplicationArguments args) {
        var user1 = User.builder().name("Raimo Ruletti").build();
        var user2 = User.builder().name("Pamela Potti").build();

        userRepository.save(user1);
        userRepository.save(user2);

        accountService.deposit(user1, "100");
        accountService.deposit(user2, "500");
        accountService.withdraw(user2, "15.50");
        accountService.deposit(user2, "100");
    }
}
