package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.Meta;
import io.memoria.jutils.core.value.Id;
import io.memoria.jutils.core.value.Version;

import java.time.LocalDateTime;

public interface UserEvent extends Event {

  record AccountCreated(Id id, Id accountId, int age) implements UserEvent {
    @Override
    public Meta meta() {
      return new Meta(LocalDateTime.of(2020, 1, 1, 1, 0), new Version());
    }
  }

  record FriendAdded(Id id, Id friendId) implements UserEvent {
    @Override
    public Meta meta() {
      return new Meta(LocalDateTime.of(2020, 1, 1, 1, 0), new Version());
    }
  }

  record MessageSent(Id id, Message message) implements UserEvent {
    @Override
    public Meta meta() {
      return new Meta(LocalDateTime.of(2020, 1, 1, 1, 0), new Version());
    }
  }
}
