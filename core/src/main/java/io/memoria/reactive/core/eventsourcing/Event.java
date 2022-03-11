package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public interface Event extends Serializable {
  Id id();

  Id stateId();

  Id commandId();

  long timestamp();
}
