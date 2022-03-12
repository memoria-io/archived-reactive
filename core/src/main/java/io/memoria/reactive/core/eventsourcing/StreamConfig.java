package io.memoria.reactive.core.eventsourcing;

public record StreamConfig(String topic, int partition, int offset, int totalPartitions) {
  public StreamConfig {
    if (topic == null || topic.isEmpty()) {
      throw new IllegalArgumentException("Topic is null or empty");
    }
    if (partition < 0) {
      throw new IllegalArgumentException("Partition number %d is less than 0".formatted(totalPartitions));
    }
    if (offset < 0) {
      throw new IllegalArgumentException("Offset value %d is less than 0".formatted(totalPartitions));
    }
    if (totalPartitions < 1) {
      throw new IllegalArgumentException("Total number of partitions %d is less than 1".formatted(totalPartitions));
    }
  }
}
