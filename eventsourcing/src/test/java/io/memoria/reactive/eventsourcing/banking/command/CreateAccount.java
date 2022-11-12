package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.StateId;

public record CreateAccount(CommandId commandId, StateId accountId, String accountname, int balance)
        implements AccountCommand {
  public static CreateAccount of(StateId accountId, String accountname, int balance) {
    return new CreateAccount(CommandId.randomUUID(), accountId, accountname, balance);
  }
}
