package com.ronit.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BankingTransactionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingTransactionSystemApplication.class, args);
    }
}