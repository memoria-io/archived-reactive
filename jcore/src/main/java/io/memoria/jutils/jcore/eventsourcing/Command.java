package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;

public interface Command {
  Id aggId();

  Id commandId();
}
