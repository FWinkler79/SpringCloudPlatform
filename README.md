The setup in this branch shows an RSocket server that was wired up **without** Spring Boot auto-configuration (which at the time of writing this did not allow easy customization of the ServerRSocketFactory, e.g. to add lease handling, etc.).

The interesting parts are in the `rsocket-server` and `rsocket-client` projects.

# RSocket Server

In `rsocket-server` we are not specifying any `spring.rsocket.server.*` settings, since they would make Spring Boot auto-configuration jump in.

```yaml
# If we pull up an RSocket server ourselves in coding 
# we should not use these settings. Otherwise Spring Boot Auto-Configuration will
# bring up yet another WebSocket RSocket endpoint.
#
#  rsocket:
#    server:
#      mapping-path: /rsocketServer
#      transport:    websocket
```

Instead, we have written `RSocketServerConfiguration` which pretty much reproduces what Spring Boot auto-configuration would do.
This follows the basic Spring (not Spring Boot) RSocket support described [here](https://docs.spring.io/spring/docs/5.2.0.RELEASE/spring-framework-reference/web-reactive.html#rsocket-annot-responders-server).

In the configuration we declare:

### RSocketMessageHandler

```java
@Bean
public RSocketMessageHandler messageHandler(RSocketStrategies rSocketStrategies) {
  RSocketMessageHandler messageHandler = new RSocketMessageHandler();
  messageHandler.setRSocketStrategies(rSocketStrategies);
  return messageHandler;
}
```

The `RSocketMessageHandler` is a Spring class that will find all `@Controller` classes that have RSocket callbacks implemented (i.e. have methods annotated with either `@ConnectMapping` or `@MessageMapping`). The handler needs an instance of strategies which contain the encoders and decoders of message payloads.

### RSocketStrategies

```java
@Bean
public RSocketStrategies rSocketStrategies(ObjectProvider<RSocketStrategiesCustomizer> customizers) {
  RSocketStrategies.Builder builder = RSocketStrategies.builder();
  builder.routeMatcher(new PathPatternRouteMatcher());
  customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
  return builder.build();
}
```

This bean provides the strategies for the RSocketMessageHandler. The strategies contain encoders and decoders for different message mime-types.
They are implemented as `RSocketStrategiesCustomizer` beans that are injected in the bean creationg above.
The customizers mainly declare Jackson JSON and Jackson CBOR encoders and decoders, so POJOs can get automatically serialized and deserialized into either JSON or a binary JSON format.

### RSocket Server

```java
@Bean 
public Mono<CloseableChannel> rsocketServer(RSocketMessageHandler handler) {
    ServerRSocketFactory factory = RSocketFactory.receive();
    
    // apply leases.
    factory = new LeaseCustomizer().apply(factory);
    
    return factory.acceptor(handler.responder())
                  .transport(WebsocketServerTransport.create("localhost", 5555))
                  .start();
}
```
Here we actually instantiate the RSocket, using the `ServerRSocketFactory`. We use the `LeaseCustomizer` to add lease configurations to the factory.
Then we add the socket acceptor implementation provided by Spring using the `RSocketMessageHandler`'s `responder()` method.
Note that we bring up the server on port 5555 over a WebSocket binding.

In `RSocketServerApplication` we call:

```java
ApplicationContext context = springApplication.run(args);
Mono<CloseableChannel> rsocketServer = (Mono<CloseableChannel>) context.getBean("rsocketServer");
rsocketServer.block();
```

This retrieves the RSocket server's channel, that we can use to `block()` and listen on.

# RSocket Client

In `rsocket-client` we are using the `ServerConnection` bean to connect the client to the server:

```java
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
                  .connectWebSocket(URI.create("http://localhost:5555"))
                  .block();
```
Note the lease configuration and the connection via WebSocket to port 5555!

# Running the sample

To run, start the following services in order:

1. service-registry
1. config-server 
1. rsocket-server
1. rsocket-client

You will see the client sending requests to the server and using up its leased quota until the server renews it. The client gets allowed 5 requests within 7 seconds, but sends more, so some requests will fail. This shows how RSocket can be used to protect servers from being overwhelmed by client requests.

# Caveats

This setup, although closer to the original RSocket API (e.g. shown [here](https://github.com/rsocket/rsocket-java/blob/master/rsocket-examples/src/main/java/io/rsocket/examples/transport/tcp/lease/LeaseExample.java)) is not ideal in terms of integration with the rest of the Spring environment.

In fact, `rsocket-server` spins up **two** Netty instances. One by WebFlux on port 3333 and used for all Reactive Web stuff. Another on port 5555 by the RSocket server APIs. This is not an ideal setup, especially, since it might incur interprocess-communication (with unpredictable side-effects) when context information needs to be handed from one Netty instance to another. For example, consider an HTTP request received on port 3333 and processed by WebFlux whose payload needs to be forwarded to another service via RSocket. In this case, you would have to transfer the payload from one Netty context to the one used by the RSocket server socket.

We do not suggest to use this setup for production use. However, it may be useful to better understand what is going on under the hood of Spring Boot and if one wants to familiarize oneself with the basic [RSocket protocol APIs](https://github.com/rsocket/rsocket-java).
