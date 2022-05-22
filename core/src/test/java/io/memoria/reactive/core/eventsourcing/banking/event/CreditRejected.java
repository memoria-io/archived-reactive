package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.banking.command.Credit;

public record CreditRejected(EventId eventId, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return creditedAcc;
  }

  public static CreditRejected of(Credit cmd) {
    return new CreditRejected(EventId.randomUUID(), cmd.commandId(), cmd.creditedAcc(), cmd.debitedAcc(), cmd.amount());
  }
}
