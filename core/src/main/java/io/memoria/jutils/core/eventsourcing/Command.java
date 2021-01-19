package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.id.Id;

public interface Command {
  Id aggId();

  Id commandId();
}
