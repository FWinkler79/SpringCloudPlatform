package com.equalities.reservation.service.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * An RSocket Endpoint for this client.
 * RSocket can be used from browsers as well (see rsocket-js).
 * An rsocket browser client can use this endpoint and react 
 * on back pressure as well or act as a full-duplex communication
 * peer.
 */
@Slf4j
@Controller
public class RSocketEndpoint {

  private ReservationServiceClient reservationServiceclient;
  
  @Autowired
  public RSocketEndpoint(ReservationServiceClient reservationServiceclient) {
    this.reservationServiceclient = reservationServiceclient;
  } 
  
  @MessageMapping("client-make-reservation")
  public Mono<ReservationConfirmation> makeReservation( @Headers Map<String, Object> compositeMetadata, 
                                                        String reservationName,        
                                                        RSocketRequester client) {                      
  
    log.info("Received request to create reservation from client: {}", client);
    log.info("- reservation name:  {}", reservationName);
    log.info("- metadata headers:  {}", compositeMetadata);
    
    // Call the server and get the reservation.
    return reservationServiceclient.createReservation(new CreateReservationRequest(reservationName));
  }
}
