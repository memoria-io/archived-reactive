package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.id.Id;

import java.time.LocalDateTime;

public interface Event {
  Id aggId();

  LocalDateTime createdAt();

  Id eventId();
}
