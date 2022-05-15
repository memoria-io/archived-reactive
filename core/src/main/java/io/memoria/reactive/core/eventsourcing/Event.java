package io.memoria.reactive.core.eventsourcing;

import java.io.Serializable;

public interface Event extends Shardable, Serializable {
  CommandId commandId();

  EventId id();

  StateId stateId();

  long timestamp();

}
