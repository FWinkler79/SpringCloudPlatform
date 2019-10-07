package com.equalities.cloud.rsocket.client;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Component;

/**
 * Bean that creates a connection to an RSocket server / remote peer.
 */
@Component
public class ServerConnection {
  private final RSocketRequester server;

  @Autowired
  public ServerConnection(RSocketRequester.Builder rsocketRequesterBuilder, RSocketMessageHandler messageHandler) {
    
    // Create a new RSocket connection with the server.
    // We connect to the server via WebSocket and register ourselves
    // as a client that can be called back (unsolicited) by the server.
    // In other words: we are a server, too, and we are communicating with
    // the server in full duplex mode. This would not be possible with plain HTTP!
    this.server = rsocketRequesterBuilder
                  // explicitly register the RSocketMessage handler to receive callbacks from the server:
                  .rsocketFactory(factory -> factory.acceptor(messageHandler.responder())) 
                  // connect to the server via WebSockets.
                  .connectWebSocket(URI.create("http://localhost:3333/rsocketServer"))
                  .block();
  }
  
  public RSocketRequester getServer() {
    return server;
  }
}
