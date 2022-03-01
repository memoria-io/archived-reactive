package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public interface Event extends Serializable {
  long sKey();

  Id stateId();
}
