package com.example.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling

@SpringBootApplication
public class OnlineFoodDeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineFoodDeliveryApplication.class, args);
    }
}