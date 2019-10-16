This branch shows an error / issue caused by a combination of Spring Cloud Function / Spring Cloud Messaging and RSocket.

To reproduce:

1. Start RabbitMQ (using docker) with `./scripts/startRabbit.sh`
1. Start `service-registry`
1. Start `config-service` 
1. Start `rsocket-server` --> NO LONGER prints the exception this issue was about.
1. Start `rsocket-client` --> Now properly gets a lease and errors when it expires or is consumed.