package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.value.Id;

public interface Command {
  Id aggId();

  Id commandId();
}
