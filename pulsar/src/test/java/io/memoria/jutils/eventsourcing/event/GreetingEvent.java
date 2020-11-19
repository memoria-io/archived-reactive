package io.memoria.jutils.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.value.Id;

public record GreetingEvent(Id id, String senderName) implements Event {
  public GreetingEvent(String id, String senderName) {
    this(new Id(id), senderName);
  }
}
