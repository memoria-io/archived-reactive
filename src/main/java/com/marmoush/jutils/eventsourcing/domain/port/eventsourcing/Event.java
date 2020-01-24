package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing;

public class Event {
  public final String eventId;

  public Event(String eventId) {
    this.eventId = eventId;
  }
}
