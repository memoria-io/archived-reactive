package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountEvent extends Event {
  StateId accountId();

  @Override
  default StateId stateId() {
    return accountId();
  }

  @Override
  default long timestamp() {
    return 0;
  }

  record AccountClosed(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {

  }

  record AccountCreated(EventId eventId, CommandId commandId, StateId accountId, String name) implements AccountEvent {

    public static AccountCreated of(CommandId commandId, StateId accountId, String name) {
      return new AccountCreated(EventId.randomUUID(), commandId, accountId, name);
    }
  }

  record NameChanged(EventId eventId, CommandId commandId, StateId accountId, String newName) implements AccountEvent {
    public static NameChanged of(CommandId commandId, StateId accountId, String newName) {
      return new NameChanged(EventId.randomUUID(), commandId, accountId, newName);
    }
  }
}
