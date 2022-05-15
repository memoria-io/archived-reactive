package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.id.Id;

sealed interface UserEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  record AccountClosed(EventId id, CommandId commandId, StateId userId) implements UserEvent {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static AccountClosed of(CommandId commandId, StateId userId) {
      return new AccountClosed(EventId.randomUUID(), commandId, userId);
    }
  }

  record ClosureRejected(EventId id, CommandId commandId, StateId userId) implements UserEvent {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static ClosureRejected of(CommandId commandId, StateId userId) {
      return new ClosureRejected(EventId.randomUUID(), commandId, userId);
    }
  }

  record DebitConfirmed(EventId id, CommandId commandId, StateId debitedAcc) implements UserEvent {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static DebitConfirmed of(CommandId commandId, StateId debitedAcc) {
      return new DebitConfirmed(EventId.randomUUID(), commandId, debitedAcc);
    }
  }

  record CreditRejected(EventId id, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) implements UserEvent {
    @Override
    public StateId stateId() {
      return creditedAcc;
    }

    public static CreditRejected of(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) {
      return new CreditRejected(EventId.randomUUID(), commandId, creditedAcc, debitedAcc, amount);
    }
  }

  record Credited(EventId id, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) implements UserEvent {
    @Override
    public StateId stateId() {
      return creditedAcc;
    }

    public static Credited of(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) {
      return new Credited(EventId.randomUUID(), commandId, creditedAcc, debitedAcc, amount);
    }
  }

  record Debited(EventId id, CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount) implements UserEvent {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static Debited of(CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount) {
      return new Debited(EventId.randomUUID(), commandId, debitedAcc, creditedAcc, amount);
    }
  }

  record UserCreated(EventId id, CommandId commandId, StateId userId, String name, int balance) implements UserEvent {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static UserCreated of(CommandId commandId, StateId userId, String name, int balance) {
      return new UserCreated(EventId.randomUUID(), commandId, userId, name, balance);
    }
  }
}
