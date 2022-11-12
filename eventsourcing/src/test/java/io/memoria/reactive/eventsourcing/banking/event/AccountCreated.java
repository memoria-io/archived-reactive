package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.CreateAccount;

public record AccountCreated(EventId eventId, CommandId commandId, StateId accountId, String name, int balance)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static AccountCreated from(CreateAccount cmd) {
    return new AccountCreated(EventId.randomUUID(), cmd.commandId(), cmd.accountId(), cmd.accountname(), cmd.balance());
  }
}
