package io.memoria.reactive.eventsourcing;

public interface Shardable {
  default boolean isInPartition(int partition, int totalPartitions) {
    return partition == partition(totalPartitions);
  }

  default int partition(int totalPartitions) {
    var hash = (stateId().hashCode() == Integer.MIN_VALUE) ? Integer.MAX_VALUE : stateId().hashCode();
    return Math.abs(hash) % totalPartitions;
  }

  StateId stateId();
}
