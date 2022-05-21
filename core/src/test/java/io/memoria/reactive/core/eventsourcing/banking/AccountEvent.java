package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  record AccountClosed(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {
    @Override
    public StateId stateId() {
      return accountId;
    }

    public static AccountClosed of(CommandId commandId, StateId accountId) {
      return new AccountClosed(EventId.randomUUID(), commandId, accountId);
    }
  }

  record ClosureRejected(EventId eventId, CommandId commandId, StateId accountId) implements AccountEvent {
    @Override
    public StateId stateId() {
      return accountId;
    }

    public static ClosureRejected of(CommandId commandId, StateId accountId) {
      return new ClosureRejected(EventId.randomUUID(), commandId, accountId);
    }
  }

  record CreditRejected(EventId eventId, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
          implements AccountEvent {
    @Override
    public StateId stateId() {
      return creditedAcc;
    }

    public static CreditRejected of(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) {
      return new CreditRejected(EventId.randomUUID(), commandId, creditedAcc, debitedAcc, amount);
    }
  }

  record Credited(EventId eventId, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
          implements AccountEvent {
    @Override
    public StateId stateId() {
      return creditedAcc;
    }

    public static Credited of(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) {
      return new Credited(EventId.randomUUID(), commandId, creditedAcc, debitedAcc, amount);
    }
  }

  record DebitConfirmed(EventId eventId, CommandId commandId, StateId debitedAcc) implements AccountEvent {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static DebitConfirmed of(CommandId commandId, StateId debitedAcc) {
      return new DebitConfirmed(EventId.randomUUID(), commandId, debitedAcc);
    }
  }

  record Debited(EventId eventId, CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount)
          implements AccountEvent {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static Debited of(CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount) {
      return new Debited(EventId.randomUUID(), commandId, debitedAcc, creditedAcc, amount);
    }
  }

  record AccountCreated(EventId eventId, CommandId commandId, StateId accountId, String name, int balance)
          implements AccountEvent {
    @Override
    public StateId stateId() {
      return accountId;
    }

    public static AccountCreated of(CommandId commandId, StateId accountId, String name, int balance) {
      return new AccountCreated(EventId.randomUUID(), commandId, accountId, name, balance);
    }
  }
}
