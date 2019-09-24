package com.equalities.cloud.reservationservice;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ReservationService {
  
  private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

  @Autowired 
  ReservationRepository reservationRepository;
  
  public static void main(String[] args) {
    SpringApplication.run(ReservationService.class, args);
  }
  
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    logger.debug("Saving initial reservations.");
    
    Stream.of("Carl", "James", "Susan", "Jack")
          .forEach( name -> {
            reservationRepository.save(new Reservation(name)); 
          });
  }
}
