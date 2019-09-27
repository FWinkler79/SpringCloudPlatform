#!/bin/bash
echo "Starting Zipkin Trace Server."
docker container rm zipkinServer
docker run -p 9411:9411 --name zipkinServer openzipkin/zipkin
docker container ls