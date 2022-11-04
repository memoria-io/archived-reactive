package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.CloseAccount;

public record ClosureRejected(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static ClosureRejected from(CloseAccount cmd) {
    return new ClosureRejected(EventId.randomUUID(), cmd.commandId(), cmd.stateId());
  }
}
