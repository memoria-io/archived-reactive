package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaUtils {
  public static KafkaConsumer<String, String> kafkaConsumer(YamlConfigMap map) {
    return new KafkaConsumer<>(map.asYamlConfigMap("kafka").get().asJavaMap("consumer").get());
  }

  public static KafkaProducer<String, String> kafkaProducer(YamlConfigMap map) {
    return new KafkaProducer<>(map.asYamlConfigMap("kafka").get().asJavaMap("producer").get());
  }

  private KafkaUtils() {}
}
