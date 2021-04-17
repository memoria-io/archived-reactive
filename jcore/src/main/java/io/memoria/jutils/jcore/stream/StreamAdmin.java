package io.memoria.jutils.jcore.stream;

import reactor.core.publisher.Mono;

public interface StreamAdmin {
  Mono<Void> createTopic(String topic, int partitions, int replicationFactor);

  Mono<Long> currentOffset(String topic, int partition);

  Mono<Boolean> exists(String topic, int partition);

  Mono<Void> increasePartitionsTo(String topic, int partitions);

  Mono<Integer> nOfPartitions(String topic);
}
