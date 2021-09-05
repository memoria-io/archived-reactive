package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.db.Msg;
import io.memoria.reactive.core.id.Id;

import java.time.LocalDateTime;

public interface Event extends Msg {
  Id aggId();

  LocalDateTime createdAt();
}
