package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface PersonEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  record NameChanged(EventId id, CommandId commandId, StateId userId, String newName) implements PersonEvent {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static NameChanged of(CommandId commandId, StateId userId, String newName) {
      return new NameChanged(EventId.randomUUID(), commandId, userId, newName);
    }
  }

  record AccountCreated(EventId id, CommandId commandId, StateId userId, String name) implements PersonEvent {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static AccountCreated of(CommandId commandId, StateId userId, String name) {
      return new AccountCreated(EventId.randomUUID(), commandId, userId, name);
    }
  }
}
