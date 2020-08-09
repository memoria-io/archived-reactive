package io.memoria.jutils.core.eventsourcing.event;

public interface Event {
  String aggId();

  String eventId();
}
