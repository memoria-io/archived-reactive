package io.memoria.reactive.core.eventsourcing.user;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;

import java.time.LocalDateTime;

public interface UserEvent extends Event {
  @Override
  default Id eventId() {
    return Id.of(0);
  }

  record MessageSent(long id, Id userId, Id receiverId, String message) implements UserEvent {
    @Override
    public Id aggId() {
      return userId;
    }

    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 1);
    }
  }

  record UserCreated(long id, Id userId, String name) implements UserEvent {
    @Override
    public Id aggId() {
      return userId;
    }

    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 1);
    }
  }
}
