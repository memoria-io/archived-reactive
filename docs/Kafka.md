# Kafka Integration

## Kafka Docker
```bash
docker-compose -f deploy/kafka-docker-compose.yml  up
```

## Kafka shells
Download Kafka and cd into it.

```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
```
