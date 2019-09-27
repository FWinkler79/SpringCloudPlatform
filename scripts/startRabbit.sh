#!/bin/bash
echo "Starting Rabbit MQ message broker."
docker container rm rabbitBroker
docker run -d --hostname my-rabbit --name rabbitBroker -p 15672:15672 -p 5672:5672 rabbitmq:3-management
docker container ls