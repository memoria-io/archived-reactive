package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.CloseAccount;

public record AccountClosed(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static AccountClosed from(CloseAccount cmd) {
    return new AccountClosed(EventId.randomUUID(), cmd.commandId(), cmd.stateId());
  }
}
