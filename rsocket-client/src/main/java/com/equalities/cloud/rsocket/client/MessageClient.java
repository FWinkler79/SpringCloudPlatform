package com.equalities.cloud.rsocket.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

/**
 * A bean that can be used to send messages to a server
 * which will echo them back.
 */
@Component
public class MessageClient {

  private final ServerConnection connection;

  @Autowired
  public MessageClient(ServerConnection connection) {
    this.connection = connection;
  }

  /**
   * sends messages to a server endpoint dedicated for
   * this client. Note how the client name is used in the
   * message endpoint route!
   */
  public Flux<Message> send(Flux<Message> messages, String clientName) {
    //@formatter:off
    RSocketRequester rsocketServer = connection.getServer();
    return rsocketServer.route(String.format("messages.from.%s", clientName))
                        .data(messages)
                        .retrieveFlux(Message.class);
    //@formatter:on
  }
}
