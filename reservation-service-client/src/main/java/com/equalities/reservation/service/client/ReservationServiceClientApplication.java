package com.equalities.reservation.service.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
public class ReservationServiceClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceClientApplication.class, args);
	}
}
