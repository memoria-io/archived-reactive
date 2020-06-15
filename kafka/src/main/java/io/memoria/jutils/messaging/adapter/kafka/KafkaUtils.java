package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

public class KafkaUtils {
  private KafkaUtils() {}

  public static KafkaMsgConsumer kafkaMsgConsumer(YamlConfigMap map, Scheduler scheduler) {
    return new KafkaMsgConsumer(new KafkaConsumer<>(map.asYamlConfigMap("kafka").asJavaMap("consumer")),
                                scheduler,
                                Duration.ofMillis(map.asYamlConfigMap("reactorKafka")
                                                     .asLong("consumer.request.timeout")));
  }

  public static KafkaMsgProducer kafkaMsgProducer(YamlConfigMap map, Scheduler scheduler) {
    return new KafkaMsgProducer(new KafkaProducer<>(map.asYamlConfigMap("kafka").asJavaMap("producer")),
                                scheduler,
                                Duration.ofMillis(map.asYamlConfigMap("reactorKafka")
                                                     .asLong("producer.request.timeout")));
  }
}
