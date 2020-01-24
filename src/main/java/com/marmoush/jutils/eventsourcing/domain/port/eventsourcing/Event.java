package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing;

public class Event {
  public final String flowId;

  public Event(String flowId) {
    this.flowId = flowId;
  }
}
