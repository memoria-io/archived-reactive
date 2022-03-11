package io.memoria.reactive.core.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.SignalType;

import static reactor.core.publisher.SignalType.ON_COMPLETE;
import static reactor.core.publisher.SignalType.ON_ERROR;
import static reactor.core.publisher.SignalType.ON_NEXT;

public record PipelineConfig(StreamConfig eventConfig, StreamConfig commandConfig, LogConfig logConfig) {

  public record LogConfig(boolean showLine, List<SignalType> signalType) {
    public static final LogConfig DEFAULT = new LogConfig(true, List.of(ON_NEXT, ON_ERROR, ON_COMPLETE));

    public SignalType[] signalTypeArray() {
      return signalType.toJavaArray(SignalType[]::new);
    }
  }

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
}
