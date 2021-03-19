package io.memoria.jutils.jcore.eventsourcing;

import reactor.core.publisher.Mono;

public interface EventStoreAdmin {
  Mono<Boolean> exists(String topic);

  Mono<Integer> createTopic(String topic, int partitions, int replicationFactor);

  Mono<Integer> setPartitions(String topic, int partitions);
}
