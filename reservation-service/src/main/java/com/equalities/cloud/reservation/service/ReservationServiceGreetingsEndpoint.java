package com.equalities.cloud.reservation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class ReservationServiceGreetingsEndpoint {
  
  @Value("${com.equalities.greeting}")
  private String greetingMessage;
  
  @RequestMapping("/greeting")
  public String serveGreeting() {
    return greetingMessage; 
  }

}
