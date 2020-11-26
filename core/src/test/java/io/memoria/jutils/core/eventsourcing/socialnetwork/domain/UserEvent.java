package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.value.Id;
import io.memoria.jutils.core.value.Version;

import java.time.LocalDateTime;

public interface UserEvent extends Event {

  record AccountCreated(Id id, Id accountId, int age) implements UserEvent {
    @Override
    public LocalDateTime creationMoment() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }

    @Override
    public Version version() {
      return new Version();
    }
  }

  record FriendAdded(Id id, Id friendId) implements UserEvent {
    @Override
    public LocalDateTime creationMoment() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }

    @Override
    public Version version() {
      return new Version();
    }
  }

  record MessageSent(Id id, Message message) implements UserEvent {
    @Override
    public LocalDateTime creationMoment() {
      return LocalDateTime.of(2020, 1, 1, 1, 0);
    }

    @Override
    public Version version() {
      return new Version();
    }
  }
}
