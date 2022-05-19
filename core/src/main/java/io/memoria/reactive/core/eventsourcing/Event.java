package io.memoria.reactive.core.eventsourcing;

import java.io.Serializable;

public interface Event extends Shardable, Serializable {
  CommandId commandId();

  EventId eventId();

  StateId stateId();

  long timestamp();

}
