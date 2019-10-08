package com.equalities.cloud.rsocket.server;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import io.rsocket.RSocketFactory;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import reactor.core.publisher.Mono;

@Configuration
@AutoConfigureAfter(RSocketStrategiesAutoConfiguration.class)
public class RSocketServerConfiguration {

  @Bean
  RSocketRequester.Builder rsocketRequesterBuilder() {
    return RSocketRequester.builder(); 
  }
  
  @Bean
  public RSocketMessageHandler messageHandler(RSocketStrategies rSocketStrategies) {
    RSocketMessageHandler messageHandler = new RSocketMessageHandler();
    messageHandler.setRSocketStrategies(rSocketStrategies);
    return messageHandler;
  }
  
  @Bean
  public RSocketStrategies rSocketStrategies(ObjectProvider<RSocketStrategiesCustomizer> customizers) {
    RSocketStrategies.Builder builder = RSocketStrategies.builder();
    builder.routeMatcher(new PathPatternRouteMatcher());
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return builder.build();
  }
  
  @Bean 
  public Mono<CloseableChannel> rsocketServer(RSocketMessageHandler handler) {
      ServerRSocketFactory factory = RSocketFactory.receive();
      
      // apply leases.
      factory = new LeaseCustomizer().apply(factory);
      
      return factory.acceptor(handler.responder())
                    .transport(WebsocketServerTransport.create("localhost", 5555))
                    .start();
                    
  }
  
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass({ ObjectMapper.class, CBORFactory.class })
  protected static class JacksonCborStrategyConfiguration {

    private static final MediaType[] SUPPORTED_TYPES = { MediaType.APPLICATION_CBOR };

    @Bean
    @Order(0)
    @ConditionalOnBean(Jackson2ObjectMapperBuilder.class)
    public RSocketStrategiesCustomizer jacksonCborRSocketStrategyCustomizer(Jackson2ObjectMapperBuilder builder) {
      return (strategy) -> {
        ObjectMapper objectMapper = builder.createXmlMapper(false).factory(new CBORFactory()).build();
        strategy.decoder(new Jackson2CborDecoder(objectMapper, SUPPORTED_TYPES));
        strategy.encoder(new Jackson2CborEncoder(objectMapper, SUPPORTED_TYPES));
      };
    }

  }

  @Configuration(proxyBeanMethods = false)
  protected static class JacksonJsonStrategyConfiguration {

    private static final MediaType[] SUPPORTED_TYPES = { MediaType.APPLICATION_JSON,
        new MediaType("application", "*+json") };

    @Bean
    @Order(1)
    @ConditionalOnBean(ObjectMapper.class)
    public RSocketStrategiesCustomizer jacksonJsonRSocketStrategyCustomizer(ObjectMapper objectMapper) {
      return (strategy) -> {
        strategy.decoder(new Jackson2JsonDecoder(objectMapper, SUPPORTED_TYPES));
        strategy.encoder(new Jackson2JsonEncoder(objectMapper, SUPPORTED_TYPES));
      };
    }
  }
}
