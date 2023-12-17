package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.demo.repository")
public class  YsuApplication {


	public static void main(String[] args) {
		SpringApplication.run(YsuApplication.class);
	}

//ngrok http --hostname=spider-easy-vulture.ngrok-free.app 8082
}
