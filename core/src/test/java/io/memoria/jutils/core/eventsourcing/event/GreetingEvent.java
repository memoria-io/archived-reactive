package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;

public record GreetingEvent(Id id, String senderName) implements Event {
  public GreetingEvent(long i) {
    this(i + "", "name_%s".formatted(i));
  }

  public GreetingEvent(String id, String senderName) {
    this(new Id(id), senderName);
  }
}
