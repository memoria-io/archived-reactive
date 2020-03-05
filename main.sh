#!/bin/bash

docker-compose -f deploy/kafka-docker-compose.yml up -d && echo "Kafka started"

docker run -d -p 4222:4222 -p 6222:6222 -p 8222:8222 nats && echo "Nats started"

docker run -it -d \
  -p 6650:6650 \
  -p 8080:8080 \
  apachepulsar/pulsar-standalone:2.4.2 && echo "Pulsar Started"

docker run -d -p 9000:9000 sonarqube && echo "Sonarqube started"
