package com.equalities.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration;

@SpringBootApplication(exclude = ContextFunctionCatalogAutoConfiguration.class)
public class ServiceGateway {

	public static void main(String[] args) {
		SpringApplication.run(ServiceGateway.class, args);
	}
}
