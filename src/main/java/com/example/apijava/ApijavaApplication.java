package com.example.apijava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class ApijavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApijavaApplication.class, args);
    }

}
