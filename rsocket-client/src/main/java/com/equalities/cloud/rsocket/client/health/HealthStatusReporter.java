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
  public Flux<HealthStatusMessage> getStatus(RSocketRequester server) {
    logger.info("Server requested health status. Returning status 'UP' message.");
    
    // Transform the stream of pings into
    // a health status message and pong it back.
    return createHealthStatusStream();
  }
  
  private Flux<HealthStatusMessage> createHealthStatusStream() {
    return Flux.just(new HealthStatusMessage("UP"), new HealthStatusMessage("DOWN"), new HealthStatusMessage("UP"));
  }
}
