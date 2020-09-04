package com.equalities.reservation.service.client;

import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class ReservationServiceRSocketClient {

  private RSocketRequester rsocketRequester;
  
  @EventListener
  public void injectRSocketRequester(PayloadApplicationEvent<RSocketRequester> rsocketConnectionEstablishedEvent) {
    // This method will be called when the application has started and (on startup) has  
    // connected to the gateway's RSocket broker. The rsocketRequester will be customized
    // by a bean.
    this.rsocketRequester = rsocketConnectionEstablishedEvent.getPayload();
  }
  
  /**
   * Sends a "Create Reservation" request to the server and returns
   * a promise for a reservation confirmation.
   * @param request - the request to create a reservation.
   * @return the Mono with the reservation confirmation.
   */
  public Mono<ReservationConfirmation> createReservation(CreateReservationRequest request) {
    String version = "1.0";
    return rsocketRequester.route("create.reservation.{vrsn}", version) 
//                    // This is the programmatic way of influencing routing through metadata.
//                    // The declarative way is shown in reservation-service-client.yml in 
//                    // https://github.com/FWinkler79/SpringCloudPlatform-Configs
//        
//                    .metadata(rsocketGatewayBrokerClient.forwarding(builder -> { 
//                      builder.serviceName("reservation-service")
//                             .with("version", "1.0")
//                             .with("canary", "false");
//                    }))
                    .data(request)
                    .retrieveMono(ReservationConfirmation.class)
                    .log();
  }
}
