package io.memoria.reactive.core.eventsourcing.banking.command;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

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
