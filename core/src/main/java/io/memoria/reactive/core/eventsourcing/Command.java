package io.memoria.reactive.core.eventsourcing;

import java.io.Serializable;

public interface Command extends Shardable, Serializable {
  CommandId id();

  StateId stateId();

  long timestamp();

}
