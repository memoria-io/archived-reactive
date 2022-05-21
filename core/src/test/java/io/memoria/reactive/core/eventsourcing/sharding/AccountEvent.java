package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  record AccountCreated(EventId eventId, CommandId commandId, StateId userId, String name) implements AccountEvent {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static AccountCreated of(CommandId commandId, StateId userId, String name) {
      return new AccountCreated(EventId.randomUUID(), commandId, userId, name);
    }
  }

  record NameChanged(EventId eventId, CommandId commandId, StateId userId, String newName) implements AccountEvent {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static NameChanged of(CommandId commandId, StateId userId, String newName) {
      return new NameChanged(EventId.randomUUID(), commandId, userId, newName);
    }
  }
}
