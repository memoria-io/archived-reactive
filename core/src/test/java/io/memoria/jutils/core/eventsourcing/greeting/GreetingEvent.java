package io.memoria.jutils.core.eventsourcing.greeting;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.value.Id;

import java.time.LocalDateTime;

public record GreetingEvent(Id id, String senderName) implements Event {
  public GreetingEvent(long i) {
    this(i + "", "name_%s".formatted(i));
  }

  public GreetingEvent(String id, String senderName) {
    this(new Id(id), senderName);
  }

  @Override
  public LocalDateTime createdAt() {
    return LocalDateTime.of(2020, 1, 1, 1, 0);
  }
}
