This branch shows an error / issue caused by a combination of Spring Cloud Function / Spring Cloud Messaging and RSocket.

To reproduce:

1. Start `service-registry`
1. Start `config-service` 
1. Start `rsocket-server` --> prints the exception this issue is about.
1. Start `rsocket-client`  