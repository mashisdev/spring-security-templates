package com.microservices.service_example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-example")
public class ExampleController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Example Service!";
    }

    @Value("${super.secret}")
    private String mySecretValue;

    @GetMapping("/secret")
    public String getSecret() {
        return "The secret is: " + mySecretValue;
    }
}

