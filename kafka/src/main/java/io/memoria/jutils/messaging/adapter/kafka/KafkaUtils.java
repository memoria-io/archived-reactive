package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.yaml.Yaml;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Map;

public final class KafkaUtils {
  public static KafkaConsumer<String, String> consumer(Yaml config) {
    Map<String, Object> configMap = config.asYaml("consumer").get().map().toJavaMap();
    return new KafkaConsumer<>(configMap);
  }

  public static KafkaProducer<String, String> producer(Yaml config) {
    Map<String, Object> configMap = config.asYaml("producer").get().map().toJavaMap();
    return new KafkaProducer<>(configMap);
  }

  private KafkaUtils() {}
}

