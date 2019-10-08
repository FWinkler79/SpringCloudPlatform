package com.equalities.cloud.rsocket.server;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.rsocket.netty.NettyRSocketServerFactory;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.boot.rsocket.server.ServerRSocketFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.reactive.ReactorResourceFactory;

@SpringBootApplication
public class RsocketServerApplication {

	public static void main(String[] args) {
	  SpringApplication springApplication = new SpringApplication(RsocketServerApplication.class);
    
	  // Make sure we run reactive. 
    // See: https://docs.spring.io/spring-boot/docs/2.2.0.M6/reference/html/spring-boot-features.html#boot-features-web-environment
	  // springApplication.setWebApplicationType(WebApplicationType.REACTIVE); 
    
	  springApplication.run(args);	
	}
	

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
	
// Does not quite work yet. The Spring guys are only just getting this integrated into their SNAPSHOTs. 
//	
//	
//	@Bean
//	public ServerRSocketFactoryCustomizer mine() {
//	  // Here, we return a ServerRSocketFactoryCustomizer bean to influence 
//	  // how the RSocket server is configured.
//	  //
//	  // A ServerRSocketFactory is defined by rsocket-java as an API that 
//	  // is used to create a server-side RSocket, using RSocketFactory.receive(). 
//	  // Among other things, it is used to configure leases to clients as shown in this sample:
//	  // https://github.com/rsocket/rsocket-java/blob/master/rsocket-examples/src/main/java/io/rsocket/examples/transport/tcp/lease/LeaseExample.java
//	  // 
//	  // On the client side, a similar class, ClientRSocketFactory, exists.
//	  // This can be customized using the RSocketRequester.Builder's .rsocketFactory() method.
//	  // See: https://docs.spring.io/spring/docs/5.2.0.RELEASE/spring-framework-reference/web-reactive.html#rsocket-requester-client-advanced
//	  
//	  // See: org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration
//	  return new LeaseCustomizer();
//	}
}
