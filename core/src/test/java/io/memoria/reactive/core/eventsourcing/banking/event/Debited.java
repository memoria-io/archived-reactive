package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

public record Debited(EventId eventId, CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return debitedAcc;
  }

  public static Debited of(CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount) {
    return new Debited(EventId.randomUUID(), commandId, debitedAcc, creditedAcc, amount);
  }
}
