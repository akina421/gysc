package com.hla.gysc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class GyscApplication {
    public static void main(String[] args) {
        SpringApplication.run(GyscApplication.class, args);
    }
}
