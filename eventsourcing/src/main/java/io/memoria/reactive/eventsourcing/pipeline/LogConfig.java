package io.memoria.reactive.eventsourcing.pipeline;

import io.vavr.collection.List;
import reactor.core.publisher.SignalType;

import java.util.logging.Level;

import static reactor.core.publisher.SignalType.ON_COMPLETE;
import static reactor.core.publisher.SignalType.ON_ERROR;
import static reactor.core.publisher.SignalType.ON_NEXT;

public record LogConfig(Level level, boolean showLine, List<SignalType> signalType) {
  public static final LogConfig INFO = new LogConfig(Level.INFO, true, List.of(ON_NEXT, ON_ERROR));
  public static final LogConfig FINE = new LogConfig(Level.FINE, true, List.of(ON_NEXT, ON_ERROR, ON_COMPLETE));

  public SignalType[] signalTypeArray() {
    return signalType.toJavaArray(SignalType[]::new);
  }
}
