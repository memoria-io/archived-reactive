package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

public record CreditRejected(EventId eventId, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return creditedAcc;
  }

  public static CreditRejected of(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) {
    return new CreditRejected(EventId.randomUUID(), commandId, creditedAcc, debitedAcc, amount);
  }

  public static record AccountClosed(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {
    @Override
    public StateId stateId() {
      return accountId;
    }

    public static AccountClosed of(CommandId commandId, StateId accountId) {
      return new AccountClosed(EventId.randomUUID(), commandId, accountId);
    }
  }
}
