package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.value.Id;

import java.time.LocalDateTime;

public interface UserEvent extends Event {

  record AccountCreated(Id id, Id accountId, int age) implements UserEvent {
    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }
  }

  record FriendAdded(Id id, Id friendId) implements UserEvent {
    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }
  }

  record MessageSent(Id id, Message message) implements UserEvent {
    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }
  }
}
