package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.stream.StreamConfig;

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

  public StreamConfig prevEventConfig() {
    return new StreamConfig(oldEventTopic, oldPartitions);
  }

  public StreamConfig eventConfig() {
    return new StreamConfig(newEventTopic, newPartitions);
  }

  public StreamConfig commandConfig() {
    return new StreamConfig(commandTopic, newPartitions);
  }

  public StreamConfig[] streamConfigs() {
    return new StreamConfig[]{prevEventConfig(), eventConfig(), commandConfig()};
  }
}
