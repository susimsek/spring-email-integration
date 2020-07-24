package com.spring.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class SpringEmailIntegrationApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringEmailIntegrationApplication.class, args);
    }

}
