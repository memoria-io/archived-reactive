package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.banking.command.CreateAccount;

public record AccountCreated(EventId eventId, CommandId commandId, StateId accountId, String name, int balance)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static AccountCreated of(CreateAccount cmd) {
    return new AccountCreated(EventId.randomUUID(), cmd.commandId(), cmd.accountId(), cmd.accountname(), cmd.balance());
  }
}
