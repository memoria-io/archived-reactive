package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.StateId;

public record Debit(CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount)
        implements AccountCommand {
  @Override
  public StateId accountId() {
    return debitedAcc;
  }

  public static Debit of(StateId debitedAcc, StateId creditedAcc, int amount) {
    return new Debit(CommandId.randomUUID(), debitedAcc, creditedAcc, amount);
  }
}
