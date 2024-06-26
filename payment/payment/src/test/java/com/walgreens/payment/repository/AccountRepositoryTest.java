package com.walgreens.payment.repository;

import com.walgreens.payment.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void saveMethod(){
        UUID uuid = UUID.fromString("7f8df2bd-ab57-4018-8e77-ee704c354ee4");
        if( userRepository.findById(uuid).isPresent())
        {
            User user = userRepository.findById(uuid).get();
        }

        // create Account


    }

}