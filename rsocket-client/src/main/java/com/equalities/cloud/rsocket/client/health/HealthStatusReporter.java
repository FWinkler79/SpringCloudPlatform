package com.equalities.cloud.rsocket.client.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Controller
public class HealthStatusReporter {

  private static final Logger logger = LoggerFactory.getLogger(HealthStatusReporter.class);

  @MessageMapping("health.status")
  public Flux<HealthStatusMessage> getStatus(RSocketRequester server) {
    logger.info("Server requested health status. Returning status 'UP' message.");

    return createHealthStatusStream();
  }
  
  private Flux<HealthStatusMessage> createHealthStatusStream() {
    UnicastProcessor<HealthStatusMessage> healthReporter = UnicastProcessor.create();
    Flux<HealthStatusMessage> healthStatusStream = healthReporter.publish()
                                                                 .autoConnect(); // start emitting as soon and as long as we have one subscriber to the stream.
    generateHealthStatusMessages(healthReporter);
    
    return healthStatusStream;
  }
  
  private void generateHealthStatusMessages(UnicastProcessor<HealthStatusMessage> healthReporter) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while(true) {
          healthReporter.onNext(new HealthStatusMessage("UP"));
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            logger.error("Interruption error.", e);
          }
        }
      }
    }).start(); 
  }
}

