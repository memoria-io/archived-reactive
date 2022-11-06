package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.Debit;

public record Debited(EventId eventId, CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return debitedAcc;
  }

  public static Debited from(Debit cmd) {
    return new Debited(EventId.randomUUID(), cmd.commandId(), cmd.debitedAcc(), cmd.creditedAcc(), cmd.amount());
  }
}