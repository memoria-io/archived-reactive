package io.memoria.reactive.core.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.SignalType;

import java.util.logging.Level;

import static reactor.core.publisher.SignalType.ON_COMPLETE;
import static reactor.core.publisher.SignalType.ON_ERROR;
import static reactor.core.publisher.SignalType.ON_NEXT;

public record PipelineConfig(StreamConfig eventConfig, StreamConfig commandConfig, LogConfig logConfig) {

  public record StreamConfig(String topic, int partition, int offset, int totalPartitions) {}

  public record LogConfig(Level logLevel, boolean showLine, List<SignalType> signalType) {
    public static final LogConfig DEFAULT = new LogConfig(Level.INFO, true, List.of(ON_NEXT, ON_ERROR, ON_COMPLETE));

    public SignalType[] signalTypeArray() {
      return signalType.toJavaArray(SignalType[]::new);
    }
  }
}
