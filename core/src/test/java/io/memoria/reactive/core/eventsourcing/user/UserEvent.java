package io.memoria.reactive.core.eventsourcing.user;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;

import java.time.LocalDateTime;

public interface UserEvent extends Event {
  record MessageSent(Id userId, Id receiverId, String message) implements UserEvent {
    @Override
    public Id aggId() {
      return userId;
    }

    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 1);
    }
  }

  record UserCreated(Id userId, String name) implements UserEvent {
    @Override
    public Id aggId() {
      return userId;
    }

    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 1);
    }
  }

  @Override
  default Id id() {
    return Id.of(0);
  }
}
