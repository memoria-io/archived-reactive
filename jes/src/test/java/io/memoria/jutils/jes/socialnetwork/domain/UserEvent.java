package io.memoria.jutils.jes.socialnetwork.domain;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;

import java.time.LocalDateTime;

public interface UserEvent extends Event {

  record AccountCreated(Id eventId, Id aggId, int age) implements UserEvent {
    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }
  }

  record FriendAdded(Id eventId, Id aggId, Id friendId) implements UserEvent {
    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }
  }

  record MessageSent(Id eventId, Id aggId, Message message) implements UserEvent {
    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }
  }
}
