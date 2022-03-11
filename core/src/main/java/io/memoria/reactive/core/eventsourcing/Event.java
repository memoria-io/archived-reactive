package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public interface Event extends Serializable {
  Id commandId();

  Id id();

  Id stateId();

  long timestamp();
}
