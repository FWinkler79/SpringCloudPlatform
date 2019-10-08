package com.equalities.cloud.rsocket.server;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * A simple endpoint that clients can fire requests at.
 * Based on the leases they got from the server, the requests may fail.
 * This is intended, and shows how leasing works in RSocket.
 */
@Controller
@Slf4j
public class LeaseTestEndpoint {

  @MessageMapping("lease.test")
  public Mono<Void> receiveRequest(Mono<Void> request) {
    log.info("Received request on 'lease.test' endpoint.");
    return Mono.empty();
  }
}

