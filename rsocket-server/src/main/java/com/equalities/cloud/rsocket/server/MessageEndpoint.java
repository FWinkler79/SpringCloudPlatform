package com.equalities.cloud.rsocket.server;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class MessageEndpoint {

  private static final Logger logger = LoggerFactory.getLogger(MessageEndpoint.class);

  @ConnectMapping
  Mono<Void> handleConnectionSetup(RSocketRequester client) {

    logger.info("Received connection from client {}", client);

    // Client can now be used to communicated back to.
    // Here we see that RSocket really goes both ways!
    // The distinction between client and server gets blurry.

    // Request a stream of health status events (ACKs)
    // from the client. But don't subscribe to it yet...
    client.route("health.status")
          .data(Mono.empty())
          .retrieveFlux(HealthStatusMessage.class)
          .subscribe(healthStatusMessage -> {
            logger.info("Client health status is '{}'", healthStatusMessage.getStatus());
          });

    return Mono.empty();
  }

  @MessageMapping("messages.for.{name}")
  public Flux<Message> receiveMessages(@DestinationVariable String name, // Note: the name of the mapping can be dynamic!
                                       Flux<Message> messages, // Note: the input params can be streams themselves!
                                       RSocketRequester client // Note: the client can get injected and be used like a remote server (e.g. for asking back)
                                      ) {
    logger.info("Messages received on channel for {}", name);
    
    // Subscribe to a stream of reception
    // status events (ACKs) from the client.
//      client.route("message.reception.status")
//            .data(messages.share())
//            .retrieveFlux(RecvStatus.class)
//            .subscribe( recvStatus -> { 
//              logger.info("Message '{}' was {}received", recvStatus.getReceivedMessage(), (recvStatus.getReceivedStatus() ? "" : "not"));
//            });

    // Echo back the messages to the
    // client (which will acknowledge them).
    return messages;
  }
}
