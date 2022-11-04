package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.Credit;

public record Credited(EventId eventId, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return creditedAcc;
  }

  public static Credited from(Credit cmd) {
    return new Credited(EventId.randomUUID(), cmd.commandId(), cmd.creditedAcc(), cmd.debitedAcc(), cmd.amount());
  }
}
