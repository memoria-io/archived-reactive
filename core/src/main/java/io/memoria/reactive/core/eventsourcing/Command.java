package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.db.Msg;
import io.memoria.reactive.core.id.Id;

public interface Command extends Msg {
  Id aggId();
}
