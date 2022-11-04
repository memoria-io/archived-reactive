package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.Credit;

public record CreditRejected(EventId eventId, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return creditedAcc;
  }

  public static CreditRejected from(Credit cmd) {
    return new CreditRejected(EventId.randomUUID(), cmd.commandId(), cmd.creditedAcc(), cmd.debitedAcc(), cmd.amount());
  }
}
