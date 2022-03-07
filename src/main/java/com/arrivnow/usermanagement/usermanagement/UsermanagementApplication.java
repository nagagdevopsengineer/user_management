package com.arrivnow.usermanagement.usermanagement;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
@EnableDiscoveryClient
public class UsermanagementApplication {

	@Autowired
	DiscoveryClient discoveryClient;
	
	public static void main(String[] args) {
		SpringApplication.run(UsermanagementApplication.class, args);
	}
	
	
	public Optional<URI> serviceUrl(String service) {
        return discoveryClient.getInstances(service)
            .stream()
            .findFirst()
            .map(si -> si.getUri());
    }

}
