package com.equalities.cloud.rsocket.client;

import static java.time.Duration.ofSeconds;

import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Component;

import io.rsocket.lease.Lease;
import io.rsocket.lease.LeaseStats;
import io.rsocket.lease.Leases;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Bean that creates a connection to an RSocket server / remote peer.
 * Also shows how to handle leases. In this example the client gets leases
 * from the server, making sure that the server cannot get overwhelmed by
 * too many client connection requests. Also, the client issues leases to 
 * the server, in case it calls back to the client.
 * The leases example is based on this sample application of rsocket-java:
 * https://github.com/rsocket/rsocket-java/blob/master/rsocket-examples/src/main/java/io/rsocket/examples/transport/tcp/lease/LeaseExample.java
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
                  .rsocketFactory(factory -> {
                    // explicitly register the RSocketMessage handler to receive callbacks from the server:
                    factory.acceptor(messageHandler.responder());
                    // emit and receive leases: (Not working yet. The Spring guys are still integrating this in their SNAPSHOTs).
                    factory.lease(() -> Leases.<NoopStats>create()
                                              .sender(new LeaseSender("Client", 10_000, 2))
                                              .receiver(new LeaseReceiver("Client")));
                  }) 
                  // connect to the server via WebSockets.
                  .connectTcp("localhost", 9999)
                  .block();
  }
  
  public RSocketRequester getServer() {
    return server;
  }
  
  private static class NoopStats implements LeaseStats {
    @Override
    public void onEvent(EventType eventType) {}
  }
  
  /**
   * Class responsible for issuing leases.
   */
  @Slf4j
  private static class LeaseSender implements Function<Optional<NoopStats>, Flux<Lease>> {
    private final String tag;
    private final int ttlMillis;
    private final int allowedRequests;

    public LeaseSender(String tag, int ttlMillis, int allowedRequests) {
      this.tag = tag;
      this.ttlMillis = ttlMillis;
      this.allowedRequests = allowedRequests;
    }

    @Override
    public Flux<Lease> apply(Optional<NoopStats> leaseStats) {
      log.info("{} stats are {}", tag, leaseStats.isPresent() ? "present" : "absent");
      //@formatter:off
      return Flux.interval(ofSeconds(1), ofSeconds(10))
                 .onBackpressureLatest()
                 .map( tick -> {
                    log.info("{} responder sends new leases: ttl: {}, requests: {}", tag, ttlMillis, allowedRequests);
                    return Lease.create(ttlMillis, allowedRequests);
                 });
      //@formatter:on
    }
  }

  /**
   * Class responsible for receiving leases.
   * The information received here can be used by 'decent' clients
   * to stop requesting new connections when their leases are depleted.
   * Clients that do not honor leases will receive an error if they 
   * try to received a new connection without a valid lease. But it 
   * would be better if they do not even try, if they know they will fail.
   */
  @Slf4j
  private static class LeaseReceiver implements Consumer<Flux<Lease>> {
    private final String tag;

    public LeaseReceiver(String tag) {
      this.tag = tag;
    }

    @Override
    public void accept(Flux<Lease> receivedLeases) {
      //@formatter:off
      receivedLeases.subscribe(lease -> log.info("{} received leases - ttl: {}, requests: {}", tag, lease.getTimeToLiveMillis(), lease.getAllowedRequests()));
      //@formatter:on
    }
  }
}
