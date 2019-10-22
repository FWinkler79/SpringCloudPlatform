package com.equalities.reservation.service.client;

import static java.time.Duration.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import reactor.core.publisher.Flux;
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
  
  @GetMapping("/healthStatus")
  @ResponseBody
  public Flux<String> getHealthStatusStream() {
    // every second send a new echo message back to  
    // the browser in a long-lasting connection.
    return Flux.interval(ofSeconds(1))
               .map((tick) -> "Status: UP<br>");
  }
}
