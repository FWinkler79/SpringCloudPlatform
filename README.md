In this project setup we show how lease handling can be implemented in an RSocket client and server using Spring Boot.
The setup shows it for an RSocket client-server setup using a TCP binding. For WebSocket, this is trickier and is shown on the **master** branch.

# Project Setup

The `rsocket-server` is setup to bind to a TCP port in `bootstrap.yml`:

```yaml
spring:     
  rsocket:
    server:
      port: 9999
      transport: tcp
```

In class `RSocketServerApplication` we re-declare / override the `RSocketServerFactory` bean creation that Spring Boot auto-configuration would otherwise declare in `RSocketServerAutoConfiguration#EmbeddedServerAutoConfiguration`:

```java
@Bean
RSocketServerFactory rSocketServerFactory(RSocketProperties properties, ReactorResourceFactory resourceFactory, ObjectProvider<ServerRSocketFactoryCustomizer> customizers) {
  NettyRSocketServerFactory factory = new NettyRSocketServerFactory();
  
  factory.setResourceFactory(resourceFactory);
  factory.setTransport(properties.getServer().getTransport());
  
  PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
  map.from(properties.getServer().getAddress()).to(factory::setAddress);
  map.from(properties.getServer().getPort()).to(factory::setPort);
  
  Collection<ServerRSocketFactoryCustomizer> serverCustomizers = customizers.orderedStream().collect(Collectors.toList());
  serverCustomizers.add(new LeaseCustomizer());
  
  factory.setServerCustomizers(serverCustomizers);
  return factory;
}
```

Here we add our `LeaseCustomizer` to the list of `ServerRSocketFactoryCustomizer`s. Basically, this has the same effect as using [this basic rsocket-java API](https://github.com/rsocket/rsocket-java/blob/master/rsocket-examples/src/main/java/io/rsocket/examples/transport/tcp/lease/LeaseExample.java) to set a lease:

```java
factory.lease(() -> Leases.<NoopStats>create()
                          .sender(new LeaseSender(SERVER_TAG, 7_000, 5))
                          .receiver(new LeaseReceiver(SERVER_TAG))
                          .stats(new NoopStats()))
```

In the `rsocket-client` we are configuring the client-to-server connection in class `ServerConnection`:
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
                  .connectTcp("localhost", 9999)
                  .block();
```

Note how the lease configuration is also added to the `ClientRSocketFactory` and how the connection is made over a TCP binding to the host and port of RSocket server.

# Running the Sample
To run the sample start the following services in order:
1. service-registry
1. config-server
1. rsocket-server
1. rsocket-client

You will see the client sending periodic requests to the server. It gets assigned a lease from the server stating a number of allowed requests and a time to live before a new lease needs to be requested. The client will send more requests than allowed by the server, and you will see the number of available requests (according to lease) go down until requests from the client actually result in an error. 

This shows how RSocket leases can protect servers from being overwhelmed by client requests.
Note that also clients can grant leases, in case servers call them back - remember: RSocket supports duplex! 😉