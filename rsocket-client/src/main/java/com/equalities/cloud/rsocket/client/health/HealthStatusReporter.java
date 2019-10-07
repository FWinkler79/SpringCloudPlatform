package com.equalities.cloud.rsocket.client.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

import com.equalities.cloud.rsocket.client.ServerConnection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

/**
 * An endpoint that a server can call back to
 * to subscribe to a stream of health events of
 * this client. 
 * 
 * Note, that for this to work, the
 * client needs to register client responders.
 * See {@link ServerConnection} for details. 
 */
@Controller
public class HealthStatusReporter {

  private static final Logger logger = LoggerFactory.getLogger(HealthStatusReporter.class);

  /**
   * Registers a new RSocket endpoint that returns a stream of 
   * health status events.
   * 
   * @param server - the RSocket server. 
   * @return the stream of health events.
   */
  @MessageMapping("health.status")
  public Flux<HealthStatusMessage> getStatus(RSocketRequester server) {
    logger.info("Server requested health status. Returning status 'UP' message.");

    return createHealthStatusStream();
  }
  
  /**
   * Creates a stream of health events.
   * @return the stream of health events.
   */
  private Flux<HealthStatusMessage> createHealthStatusStream() {
    UnicastProcessor<HealthStatusMessage> healthReporter = UnicastProcessor.create();
    Flux<HealthStatusMessage> healthStatusStream = healthReporter.publish()
                                                                 .autoConnect(); // start emitting as soon and as long as we have one subscriber to the stream.
    generateHealthStatusMessages(healthReporter);
    
    return healthStatusStream;
  }
  
  /**
   * Plain and simple thread-based generation of {@link HealthStatusMessage} instances
   * that are pumped into the stream of health events.
   * @param healthReporter - the health status reporter to publish events to the stream.
   */
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

