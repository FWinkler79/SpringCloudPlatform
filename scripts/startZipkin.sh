#!/bin/bash
docker container rm zipkinServer
docker run -p 9411:9411 --name zipkinServer openzipkin/zipkin