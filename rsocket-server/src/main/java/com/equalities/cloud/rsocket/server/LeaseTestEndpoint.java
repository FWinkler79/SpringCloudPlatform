package com.equalities.cloud.rsocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Mono;

/**
 * A simple endpoint that clients can fire requests at.
 * Based on the leases they got from the server, the requests may fail.
 * This is intended, and shows how leasing works in RSocket.
 */
@Controller
public class LeaseTestEndpoint {

  private static final Logger logger = LoggerFactory.getLogger(LeaseTestEndpoint.class);
  
  @MessageMapping("lease.test")
  public Mono<Void> receiveRequest(Mono<Void> request) {
    return Mono.empty();
  }
}

