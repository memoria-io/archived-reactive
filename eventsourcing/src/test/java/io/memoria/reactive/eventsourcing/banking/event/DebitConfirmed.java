package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.ConfirmDebit;

public record DebitConfirmed(EventId eventId, CommandId commandId, StateId debitedAcc) implements AccountEvent {
  @Override
  public StateId stateId() {
    return debitedAcc;
  }

  public static DebitConfirmed from(ConfirmDebit cmd) {
    return new DebitConfirmed(EventId.randomUUID(), cmd.commandId(), cmd.debitedAcc());
  }
}
