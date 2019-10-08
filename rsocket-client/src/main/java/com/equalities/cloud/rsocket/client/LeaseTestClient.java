package com.equalities.cloud.rsocket.client;

import static java.time.Duration.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A bean that can be used to send messages to a server
 * which will echo them back.
 */
@Component
@Slf4j
public class LeaseTestClient {

  private final ServerConnection connection;

  @Autowired
  public LeaseTestClient(ServerConnection connection) {
    this.connection = connection;
  }

  /**
   * sends messages to a server endpoint dedicated for
   * this client. Note how the client name is used in the
   * message endpoint route!
   */
  public void fireRequests(int periodInSeconds) {
    //@formatter:off
    RSocketRequester rsocketServer = connection.getServer();
    
    Flux.interval(ofSeconds(periodInSeconds))
        .flatMap( signal -> {
            log.info("Client requester availability: " + rsocketServer.rsocket().availability());
            Mono<Void> returnValue = rsocketServer.route("lease.test")
                .data(Mono.empty())
                .retrieveMono(Void.class)
                .doOnError(err -> log.info("Client request error: " + err))
                .onErrorResume(err -> Mono.empty());
            
            return returnValue;
        })
        .subscribe(resp -> log.info("Client successfully received a response from server."));
        
    //@formatter:on
  }
}
