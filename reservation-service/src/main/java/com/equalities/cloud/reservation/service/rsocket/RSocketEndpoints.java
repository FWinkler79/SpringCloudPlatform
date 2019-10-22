package com.equalities.cloud.reservation.service.rsocket;

import static com.equalities.cloud.reservation.service.rsocket.ReservationConfirmation.Status.BOOKED;

import java.util.Map;

import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class RSocketEndpoints {
  
  @ConnectMapping
  Mono<Void> handleConnectionSetup(RSocketRequester client,                          // The client making the connection request. Can be used for full duplex communication.
                                   @Headers Map<String, Object> compositeMetadata) { // The metadata used for connection setup. The Object in the map is created by instances of MetadataExtractor / Decoder.

    log.info("Received connection SETUP request. ");
    log.info("- metadata: {}", compositeMetadata);
    return Mono.empty();
  }

  @MessageMapping("create.reservation")
  public Mono<ReservationConfirmation> createReservation(
      @Headers Map<String, Object> compositeMetadata, // Note: you can get access to the metadata of RSocket frames. (see: https://docs.spring.io/spring/docs/5.2.0.RELEASE/spring-framework-reference/web-reactive.html#rsocket-annot-messagemapping)
      CreateReservationRequest request,        
      RSocketRequester client) {                      // Note: the client can get injected and be used like a remote server (e.g. for asking back)
  
    log.info("Received request to create reservation from client: {}", client);
    log.info("- request:           {}", request);
    log.info("- metadata headers:  {}", compositeMetadata);
    
    // Return a confirmation that reservation was successful.
    return Mono.just(new ReservationConfirmation(request.getReservationName(), BOOKED));
  }
}
