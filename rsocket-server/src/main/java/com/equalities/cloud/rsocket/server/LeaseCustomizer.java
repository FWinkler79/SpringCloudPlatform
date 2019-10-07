package com.equalities.cloud.rsocket.server;

import static java.time.Duration.*;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.boot.rsocket.server.ServerRSocketFactoryCustomizer;

import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.lease.Lease;
import io.rsocket.lease.LeaseStats;
import io.rsocket.lease.Leases;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * A ServerRSocketFactoryCustomizer to add the emission 
 * (and retrieval) of leases to (and from) clients.
 * Leases can be used to limit the number of accepted clients
 * on server side. This will keep the server responsive for
 * more, distinct clients, and keeps it from being overwhelmed
 * with requests.
 * 
 * The implementation is based on this sample:
 * https://github.com/rsocket/rsocket-java/blob/master/rsocket-examples/src/main/java/io/rsocket/examples/transport/tcp/lease/LeaseExample.java
 */
public class LeaseCustomizer implements ServerRSocketFactoryCustomizer {

  @Override
  public ServerRSocketFactory apply(ServerRSocketFactory factory) {
    factory.lease(() -> Leases.<NoopStats>create()
                              .sender(new LeaseSender("Server", 7_000, 5))
                              .receiver(new LeaseReceiver("Server")));
    
    return factory;
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

