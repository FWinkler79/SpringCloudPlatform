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
  private Map<RSocketRequester, Flux<HealthStatusMessage>> healthStatusByClient = new HashMap<>();

  @ConnectMapping
  Mono<Void> handleConnectionSetup(RSocketRequester client) {

    logger.info("Received connection from client {}", client);

    // The client can now be used to communicated back to!
    // Here we see that RSocket really goes both ways!
    // The lines between client and server get blurry.

    // Request a stream of health status events from the client. 
    // Note, although we could subscribe to the stream right away,
    // we show here, how this could be deferred to a later stage.
    // The actual subscription to the stream is only done later in
    // method 'receiveMessages'.
    Flux<HealthStatusMessage> healthStatusStream = client.route("health.status")
                                                         .data(Mono.empty())
                                                         .retrieveFlux(HealthStatusMessage.class)
                                                         .doOnNext(healthStatusMessage -> {
                                                           logger.info("Client health status is '{}'", healthStatusMessage.getStatus());
                                                         });
    
    healthStatusByClient.put(client, healthStatusStream);
    
    return Mono.empty();
  }

  @MessageMapping("messages.from.{clientName}")
  public Flux<Message> receiveMessages(@DestinationVariable String clientName, // Note: the name of the mapping can be dynamic!
                                       Flux<Message> messages, // Note: the input params can be streams themselves!
                                       RSocketRequester client // Note: the client can get injected and be used like a remote server (e.g. for asking back)
                                      ) {
    
    logger.info("Messages received on channel from client with name '{}'", clientName);
    logger.info("- Client: {}", client);
    
    // Subscribe to the client's health status stream only now.
    healthStatusByClient.get(client).subscribe();
    
    // Echo back the messages to the client.
    return messages;
  }
}
