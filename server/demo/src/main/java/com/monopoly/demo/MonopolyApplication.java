package com.monopoly.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController  // macht diese Klasse gleichzeitig zum Controller
public class MonopolyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonopolyApplication.class, args);
    }

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }
}
