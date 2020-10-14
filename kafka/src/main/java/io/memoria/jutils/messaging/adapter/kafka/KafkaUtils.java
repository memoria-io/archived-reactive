package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.transformer.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Map;

public final class KafkaUtils {
  public static KafkaConsumer<String, String> consumer(Properties config) {
    Map<String, Object> configMap = config.sub("consumer").get().map().toJavaMap();
    return new KafkaConsumer<>(configMap);
  }

  public static KafkaProducer<String, String> producer(Properties config) {
    Map<String, Object> configMap = config.sub("producer").get().map().toJavaMap();
    return new KafkaProducer<>(configMap);
  }

  private KafkaUtils() {}
}

