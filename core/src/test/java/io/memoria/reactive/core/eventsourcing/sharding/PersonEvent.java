package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface PersonEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  record NameChanged(Id id, Id commandId, Id userId, String newName) implements PersonEvent {
    @Override
    public Id stateId() {
      return userId;
    }

    public static NameChanged of(Id commandId, Id userId, String newName) {
      return new NameChanged(Id.of(UUID.randomUUID()), commandId, userId, newName);
    }
  }

  record AccountCreated(Id id, Id commandId, Id userId, String name) implements PersonEvent {
    @Override
    public Id stateId() {
      return userId;
    }

    public static AccountCreated of(Id commandId, Id userId, String name) {
      return new AccountCreated(Id.of(UUID.randomUUID()), commandId, userId, name);
    }
  }
}
