package io.memoria.jutils.jcore.eventsourcing.data.user;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;

import java.time.LocalDateTime;

public interface UserEvent extends Event {

  record UserCreated(Id eventId, String name) implements Event {
    @Override
    public Id aggId() {
      return Id.of("ignored");
    }

    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 1);
    }
  }
}
