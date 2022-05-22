package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

public record ClosureRejected(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static ClosureRejected of(CommandId commandId, StateId accountId) {
    return new ClosureRejected(EventId.randomUUID(), commandId, accountId);
  }
}
