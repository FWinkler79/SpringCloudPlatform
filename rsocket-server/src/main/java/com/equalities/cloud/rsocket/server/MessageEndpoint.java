package com.equalities.cloud.rsocket.server;

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
      
      // client could now be used to negotiate 
      // connection setup. Here we see that RSocket
      // really goes both ways. The distinction between
      // client and server gets blurry.
      /*
      client.route("nextStatusReports").data("5")
          .retrieveFlux(StatusReport.class)
          .subscribe( statusReport -> { 
              // ...
          });
      */
      
      return Mono.empty(); 
  }
  
  @MessageMapping("messages.for.{name}")
  public Flux<Message> receiveMessages(@DestinationVariable String name, // Note: the name of the mapping can be dynamic! 
                                       Flux<Message> messages,           // Note: the input params can be streams themselves! 
                                       RSocketRequester client           // Note: the client can get injected and be used like a remote server (e.g. for asking back)
                                      ) {
    
      logger.info("Messages received on channel for {}", name);
      return messages;
  }
}
