package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;

import java.io.Serializable;

public interface Command extends Serializable {
  Id aggId();

  Id commandId();
}
