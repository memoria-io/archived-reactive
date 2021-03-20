package io.memoria.jutils.jcore.msgbus;

import reactor.core.publisher.Mono;

public interface MsgBusAdmin {
  Mono<Void> createTopic(String topic, int partitions, int replicationFactor);

  Mono<Long> currentOffset(String topic, int partition);

  Mono<Boolean> exists(String topic);

  Mono<Integer> nOfPartitions(String topic);
}
