package com.equalities.cloud.rsocket.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class RsocketClientApplication {
   
  @Autowired
  private MessageClient messageClient;
  
  @Autowired
  private LeaseTestClient leaseTestClient;

  public static void main(String[] args) {

    SpringApplication springApplication = new SpringApplication(RsocketClientApplication.class);

    // Make sure we run reactive.
    // See:
    // https://docs.spring.io/spring-boot/docs/2.2.0.M6/reference/html/spring-boot-features.html#boot-features-web-environment
    // springApplication.setWebApplicationType(WebApplicationType.REACTIVE);

    ApplicationContext context = springApplication.run(args);
    
    RsocketClientApplication thisApplication = context.getBean(RsocketClientApplication.class);
    thisApplication.sendMessages();
    thisApplication.testLeases();
  }
  
  private void sendMessages() {
    //@formatter:off
    Flux<Message> messages = Flux.just( new Message("Hello"), 
                                        new Message("World"), 
                                        new Message("Welcome to RSocket!"), 
                                        new Message("All these messages are streamed with backpressure."));
    
    Flux<Message> echos = messageClient.send(messages, "RSocket-Client-1");
    
    echos.log()
         .limitRate(2)
         .subscribe();
    //@formatter:on
  }
  
  private void testLeases() {
    // fire a request at the server 1 every second.
    leaseTestClient.fireRequests(1);
  }
}
