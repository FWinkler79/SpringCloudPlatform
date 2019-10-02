package com.equalities.cloud.rsocket.client;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

@Component
public class MessageClient {

    private final RSocketRequester server;

    @Autowired
    public MessageClient(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.server = rsocketRequesterBuilder.connectWebSocket(URI.create("http://localhost:3333/rsocketServer")).block();
    }

    public Flux<Message> echo(Flux<Message> messages) {
        return server.route("messages.for.you").data(messages).retrieveFlux(Message.class);
    }
}
