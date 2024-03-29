package io.memoria.reactive.kafka;

import io.memoria.reactive.eventsourcing.repo.Stream;
import io.vavr.collection.Map;

import java.util.function.Supplier;

public interface KafkaStream extends Stream {
  static Stream create(Map<String, Object> producerConfig,
                       Map<String, Object> consumerConfig,
                       Supplier<Long> timeSupplier) {
    return new DefaultKafkaStream(producerConfig, consumerConfig, timeSupplier);
  }
}
