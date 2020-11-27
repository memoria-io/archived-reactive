package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;

import java.time.LocalDateTime;

public interface Event {
  LocalDateTime createdAt();

  Id id();
}
