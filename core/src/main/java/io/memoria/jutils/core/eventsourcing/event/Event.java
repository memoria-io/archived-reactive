package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;

public interface Event {
  Id id();

  Meta meta();
}
