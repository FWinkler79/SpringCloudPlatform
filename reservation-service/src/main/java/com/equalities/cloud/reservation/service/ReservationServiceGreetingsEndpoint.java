package com.equalities.cloud.reservation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class ReservationServiceGreetingsEndpoint {
  
  //TODO: This is not a good way to inject configurations. 
  //      It creates a tight deployment dependency to the config-server 
  //      being present (and visible within Eureka) when this application starts up.
  //      If this is not (yet) the case, startup will fail. This should not be the
  //      case, and the startup order must not be enforced.
  @Value("${com.equalities.greeting}")
  private String greetingMessage;
  
  @RequestMapping("/greeting")
  public String serveGreeting() {
    return greetingMessage; 
  }

}
