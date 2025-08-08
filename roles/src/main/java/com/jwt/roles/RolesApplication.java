package com.jwt.roles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RolesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RolesApplication.class, args);
	}

}
