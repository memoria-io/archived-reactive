package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface Event extends Serializable {
  Id aggId();

  LocalDateTime createdAt();

  Id eventId();
}
