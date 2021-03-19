package io.memoria.jutils.jkafka.user;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;

import java.time.LocalDateTime;

record UserCreated(Id eventId, String name) implements Event {
  public UserCreated(long i) {
    this(Id.of(i), "name" + i);
  }

  @Override
  public Id aggId() {
    return Id.of("ignored");
  }

  @Override
  public LocalDateTime createdAt() {
    return LocalDateTime.of(2020, 1, 1, 1, 1);
  }
}
