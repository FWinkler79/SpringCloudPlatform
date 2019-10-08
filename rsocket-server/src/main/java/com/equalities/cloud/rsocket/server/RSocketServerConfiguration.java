package com.equalities.cloud.rsocket.server;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.rsocket.CustomRSocketWebSocketNettyRouteProvider;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.boot.rsocket.server.ServerRSocketFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

import io.rsocket.SocketAcceptor;

//Important auto-configurations:
//RSocketServerAutoConfiguration
//RSocketMessagingAutoConfiguration
//WebFluxServerAutoConfiguration

@Configuration
public class RSocketServerConfiguration {

  @Bean
  CustomRSocketWebSocketNettyRouteProvider rSocketWebsocketRouteProvider(RSocketProperties properties, 
                                                                         RSocketMessageHandler messageHandler, 
                                                                         ObjectProvider<ServerRSocketFactoryCustomizer> customizers) {
    
    String mappingPath = properties.getServer().getMappingPath();
    SocketAcceptor rSocketAcceptor = messageHandler.responder();
    
    CustomRSocketWebSocketNettyRouteProvider routeProvider = new CustomRSocketWebSocketNettyRouteProvider(mappingPath, rSocketAcceptor);
    List<ServerRSocketFactoryCustomizer> rSocketServerFactoryCustomizers = customizers.orderedStream().collect(Collectors.toList());
    
    // add our own customizer.
    rSocketServerFactoryCustomizers.add(new LeaseCustomizer());
    
    routeProvider.setCustomizers(rSocketServerFactoryCustomizers);
    return routeProvider;
  }
}
