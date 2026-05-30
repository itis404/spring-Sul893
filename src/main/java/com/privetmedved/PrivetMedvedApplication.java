package com.privetmedved;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PrivetMedvedApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrivetMedvedApplication.class, args);
    }
}