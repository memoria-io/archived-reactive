package io.memoria.jutils.core.eventsourcing.greeting;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.Meta;
import io.memoria.jutils.core.value.Id;
import io.memoria.jutils.core.value.Version;

import java.time.LocalDateTime;

public record GreetingEvent(Id id, String senderName) implements Event {
  public GreetingEvent(long i) {
    this(i + "", "name_%s".formatted(i));
  }

  public GreetingEvent(String id, String senderName) {
    this(new Id(id), senderName);
  }

  @Override
  public Meta meta() {
    return new Meta(LocalDateTime.of(2020, 1, 1, 1, 0), new Version());
  }
}
