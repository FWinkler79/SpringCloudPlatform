package com.equalities.cloud.reservation.service.rsocket;

import static com.equalities.cloud.reservation.service.rsocket.ReservationConfirmation.Status.BOOKED;

import java.util.Map;

import org.bouncycastle.crypto.engines.TnepresEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;

import com.equalities.cloud.reservation.service.Reservation;
import com.equalities.cloud.reservation.service.ReservationRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
@Controller
public class RSocketEndpoints {
  
  private ReservationRepository reservationRepository;
  
  private Scheduler jdbcScheduler;
  
  private TransactionTemplate transactionTemplate;
  
  @Autowired
  public RSocketEndpoints(ReservationRepository reservationRepository, 
                          @Qualifier("jdbcScheduler") Scheduler jdbcScheduler, 
                          TransactionTemplate transactionTemplate) {
    this.reservationRepository = reservationRepository;
    this.jdbcScheduler = jdbcScheduler;
    this.transactionTemplate = transactionTemplate;
  }
  
  @ConnectMapping
  Mono<Void> handleConnectionSetup(RSocketRequester client,                          // The client making the connection request. Can be used for full duplex communication.
                                   @Headers Map<String, Object> compositeMetadata) { // The metadata used for connection setup. The Object in the map is created by instances of MetadataExtractor / Decoder.

    log.info("Received connection SETUP request. ");
    log.info("- metadata: {}", compositeMetadata);
    return Mono.empty();
  }

  @MessageMapping("create-reservation")
  public Mono<ReservationConfirmation> createReservation(
      @Headers Map<String, Object> compositeMetadata, // Note: you can get access to the metadata of RSocket frames. (see: https://docs.spring.io/spring/docs/5.2.0.RELEASE/spring-framework-reference/web-reactive.html#rsocket-annot-messagemapping)
      CreateReservationRequest request,        
      RSocketRequester client) {                      // Note: the client can get injected and be used like a remote server (e.g. for asking back)
  
    log.info("Received request to create reservation from client: {}", client);
    log.info("- request:           {}", request);
    log.info("- metadata headers:  {}", compositeMetadata);
    
    return Mono.fromCallable(() -> transactionTemplate.execute(status -> {
      reservationRepository.save(new Reservation(request.getReservationName()));
      return new ReservationConfirmation(request.getReservationName(), BOOKED);
    })).subscribeOn(jdbcScheduler);
  }
}
