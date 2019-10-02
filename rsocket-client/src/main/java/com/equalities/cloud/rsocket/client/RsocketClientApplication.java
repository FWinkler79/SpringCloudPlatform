package com.equalities.cloud.rsocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class RsocketClientApplication {
   
  @Autowired
  private MessageClient messageClient;

  public static void main(String[] args) {

    SpringApplication springApplication = new SpringApplication(RsocketClientApplication.class);

    // Make sure we run reactive.
    // See:
    // https://docs.spring.io/spring-boot/docs/2.2.0.M6/reference/html/spring-boot-features.html#boot-features-web-environment
    // springApplication.setWebApplicationType(WebApplicationType.REACTIVE);

    ApplicationContext context = springApplication.run(args);
    
    RsocketClientApplication thisApplication = context.getBean(RsocketClientApplication.class);
    thisApplication.sendMessages();
  }
  
  private void sendMessages() {
    Flux<Message> echoedMessages = messageClient.echo(Flux.just( new Message("Hello"), new Message("World"), new Message("Welcome to RSocket!"), new Message("All these messages are streamed with backpressure.")));
    echoedMessages.log()
                  .limitRate(2)
                  .subscribe();
  }
}
