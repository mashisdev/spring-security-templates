package com.oauth2.multi_auth;

import com.oauth2.multi_auth.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class MultiAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiAuthApplication.class, args);
	}

}
