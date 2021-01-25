package io.memoria.jutils.eventsourcing;

import io.memoria.jutils.core.id.Id;

public interface Command {
  Id aggId();

  Id commandId();
}
