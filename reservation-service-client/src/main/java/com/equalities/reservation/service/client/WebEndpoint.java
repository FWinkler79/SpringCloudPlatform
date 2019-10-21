package com.equalities.reservation.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import reactor.core.publisher.Mono;
/**
 * A "traditional" WebFlux-based Web endpoint.
 * The endpoint will basically translate a POST request
 * into an RSocket request to the server to create a 
 * reservation and receive back a confirmation.
 */
@Controller
public class WebEndpoint {

  private ReservationServiceClient reservationServiceClient;
  
  @Autowired
  public WebEndpoint(ReservationServiceClient reservationServiceClient) {
    this.reservationServiceClient = reservationServiceClient;
  }
  
  @PostMapping("/reservation/create/{reservationName}")
  @ResponseBody
  public Mono<ReservationConfirmation> makeReservation(@PathVariable String reservationName) {
    return reservationServiceClient.createReservation(new CreateReservationRequest(reservationName));
  }
}
