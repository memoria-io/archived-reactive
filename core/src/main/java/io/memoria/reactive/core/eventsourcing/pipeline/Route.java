package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.stream.StreamConfig;
import io.vavr.collection.List;

public record Route(String commandTopic,
                    int partition,
                    String oldEventTopic,
                    int oldPartitions,
                    String newEventTopic,
                    int newPartitions) {
  public Route {
    if (commandTopic == null || commandTopic.isEmpty() || newEventTopic == null || newEventTopic.isEmpty()) {
      throw new IllegalArgumentException("Topic is null or empty");
    }
    if (partition < 0) {
      throw new IllegalArgumentException("Partition number %d is less than 0".formatted(newPartitions));
    }
    if (newPartitions < 1) {
      throw new IllegalArgumentException("Total number of totalPartitions %d is less than 1".formatted(newPartitions));
    }
  }

  public StreamConfig oldEventConfig() {
    return new StreamConfig(oldEventTopic, oldPartitions);
  }

  public StreamConfig newEventConfig() {
    return new StreamConfig(newEventTopic, newPartitions);
  }

  public StreamConfig commandConfig() {
    return new StreamConfig(commandTopic, newPartitions);
  }

  public List<StreamConfig> streamConfigList() {
    return List.of(commandConfig(), oldEventConfig(), newEventConfig());
  }
}
