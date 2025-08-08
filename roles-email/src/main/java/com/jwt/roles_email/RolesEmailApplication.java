package com.jwt.roles_email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RolesEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(RolesEmailApplication.class, args);
	}

}
