package com.arrivnow.usermanagement.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
public class UsermanagementApplication {

  
	public static void main(String[] args) {
		SpringApplication.run(UsermanagementApplication.class, args);
	}

}
