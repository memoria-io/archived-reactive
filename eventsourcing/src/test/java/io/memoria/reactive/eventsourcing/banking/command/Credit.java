package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.StateId;

public record Credit(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
        implements AccountCommand {
  @Override
  public StateId accountId() {
    return creditedAcc;
  }

  public static Credit of(StateId creditedAcc, StateId debitedAcc, int amount) {
    return new Credit(CommandId.randomUUID(), creditedAcc, debitedAcc, amount);
  }
}
