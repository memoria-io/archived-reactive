package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

public class Command extends Event {
  public Command(String id) {
    super(id);
  }
}
