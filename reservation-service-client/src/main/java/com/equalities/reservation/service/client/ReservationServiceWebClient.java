package com.equalities.reservation.service.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient("reservation-service")
public interface ReservationServiceWebClient {

  @RequestMapping(method = RequestMethod.POST, value = "/reservation/create/{name}", consumes = "application/json")
  Mono<ReservationConfirmation> createReservation(@PathVariable("name") String name);
}
