package com.equalities.cloud.rsocket.client.ack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

import com.equalities.cloud.rsocket.client.Message;

import reactor.core.publisher.Flux;

@Controller
public class ReceptionStatusReporter {
  
  private static final Logger logger = LoggerFactory.getLogger(ReceptionStatusReporter.class);
    
  @MessageMapping("message.reception.status")
  public Flux<RecvStatus> getStatus(Flux<Message> messages, RSocketRequester server) {
      // Transform the stream of messages into a stream of 
      // reception status reports.
      return Flux.from(messages)
                 .map( message -> {
                   logger.info("Received message from server. Sending ACK signal.");
                   RecvStatus status = new RecvStatus(message.getText(), true);
                   return status;
                  });
  }
}
