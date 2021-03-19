package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;

import java.time.LocalDateTime;

record UserCreated(Id eventId, String name) implements Event {
  @Override
  public Id aggId() {
    return Id.of(name);
  }

  @Override
  public LocalDateTime createdAt() {
    return LocalDateTime.of(2020, 1, 1, 1, 1);
  }
}
