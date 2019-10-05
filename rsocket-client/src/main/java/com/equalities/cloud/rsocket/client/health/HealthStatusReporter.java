package com.equalities.cloud.rsocket.client.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;

@Controller
public class HealthStatusReporter {

  private static final Logger logger = LoggerFactory.getLogger(HealthStatusReporter.class);

  @MessageMapping("health.status")
  public Flux<HealthStatusMessage> getStatus(Flux<Ping> healthPings, RSocketRequester server) {
    
    // Subscribe to the health pings we receive.
    healthPings.subscribe();
    
    // Transform the stream of pings into
    // a health status message and pong it back.
    return healthPings.map(ping -> {
                         logger.info("Received health status ping. Returning status 'UP' message.");
                         HealthStatusMessage healthStatusMessage = new HealthStatusMessage("UP");
                         return healthStatusMessage;
                       });
  }
}
