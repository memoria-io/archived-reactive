package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.file.YamlConfigMap;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Map;

public final class KafkaUtils {
  public static KafkaConsumer<String, String> consumer(YamlConfigMap config) {
    Map<String, Object> configMap = config.asYamlConfigMap("consumer").get().map().toJavaMap();
    return new KafkaConsumer<>(configMap);
  }

  public static KafkaProducer<String, String> producer(YamlConfigMap config) {
    Map<String, Object> configMap = config.asYamlConfigMap("producer").get().map().toJavaMap();
    return new KafkaProducer<>(configMap);
  }

  private KafkaUtils() {}
}

