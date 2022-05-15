package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

public interface Stateful {
  default boolean isInPartition(int partition, int totalPartitions) {
    return partition == partition(totalPartitions);
  }

  default int partition(int totalPartitions) {
    return Math.abs(stateId().hashCode()) % totalPartitions;
  }

  Id stateId();
}
