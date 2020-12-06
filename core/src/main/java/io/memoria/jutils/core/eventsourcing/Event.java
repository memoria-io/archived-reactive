package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.value.Id;

import java.time.LocalDateTime;

public interface Event {
  Id aggId();

  LocalDateTime createdAt();

  Id eventId();
}
