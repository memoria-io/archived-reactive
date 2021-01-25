package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;

import java.time.LocalDateTime;

public interface Event {
  Id aggId();

  LocalDateTime createdAt();

  Id eventId();
}
