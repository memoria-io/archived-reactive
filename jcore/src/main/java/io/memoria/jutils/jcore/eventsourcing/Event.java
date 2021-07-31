package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.stream.Msg;

import java.time.LocalDateTime;

public interface Event extends Msg {
  Id aggId();

  LocalDateTime createdAt();

  Id eventId();
}
