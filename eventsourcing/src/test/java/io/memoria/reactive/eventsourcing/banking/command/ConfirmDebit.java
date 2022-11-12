package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.StateId;

public record ConfirmDebit(CommandId commandId, StateId debitedAcc) implements AccountCommand {
  @Override
  public StateId accountId() {
    return debitedAcc;
  }

  public static ConfirmDebit of(StateId debitedAcc) {
    return new ConfirmDebit(CommandId.randomUUID(), debitedAcc);
  }
}
