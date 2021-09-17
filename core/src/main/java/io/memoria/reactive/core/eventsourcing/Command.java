package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public interface Command extends Serializable {
  Id aggId();

  Id id();
}
