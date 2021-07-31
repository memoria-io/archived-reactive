package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface Event extends Serializable {
  Id aggId();

  LocalDateTime createdAt();

  Id eventId();
}
