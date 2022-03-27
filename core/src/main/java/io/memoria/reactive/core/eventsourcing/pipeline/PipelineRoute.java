package io.memoria.reactive.core.eventsourcing.pipeline;

public record PipelineRoute(String commandTopic, String eventTopic, int partition, int totalPartitions) {
  public PipelineRoute {
    if (commandTopic == null || commandTopic.isEmpty() || eventTopic == null || eventTopic.isEmpty()) {
      throw new IllegalArgumentException("Topic is null or empty");
    }
    if (partition < 0) {
      throw new IllegalArgumentException("Partition number %d is less than 0".formatted(totalPartitions));
    }
    if (totalPartitions < 1) {
      throw new IllegalArgumentException("Total number of partitions %d is less than 1".formatted(totalPartitions));
    }
  }
}
