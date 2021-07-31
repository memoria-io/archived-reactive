package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.stream.Msg;

public interface Command extends Msg {
  Id aggId();

  Id commandId();
}
