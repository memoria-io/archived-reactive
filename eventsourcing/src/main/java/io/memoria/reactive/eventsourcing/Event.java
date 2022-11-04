package io.memoria.reactive.eventsourcing;

import java.io.Serializable;

public interface Event extends Shardable, Serializable {
  CommandId commandId();

  EventId eventId();

  StateId stateId();

  long timestamp();

}
