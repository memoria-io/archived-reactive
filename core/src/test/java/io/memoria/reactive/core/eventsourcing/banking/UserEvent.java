package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface UserEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  record AccountClosed(Id id, Id commandId, Id userId) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }

    public static AccountClosed of(Id commandId, Id userId) {
      return new AccountClosed(Id.of(UUID.randomUUID()), commandId, userId);
    }
  }

  record ClosureRejected(Id id, Id commandId, Id userId) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }

    public static ClosureRejected of(Id commandId, Id userId) {
      return new ClosureRejected(Id.of(UUID.randomUUID()), commandId, userId);
    }
  }

  record DebitConfirmed(Id id, Id commandId, Id debitedAcc) implements UserEvent {
    @Override
    public Id stateId() {
      return debitedAcc;
    }

    public static DebitConfirmed of(Id commandId, Id debitedAcc) {
      return new DebitConfirmed(Id.of(UUID.randomUUID()), commandId, debitedAcc);
    }
  }

  record CreditRejected(Id id, Id commandId, Id creditedAcc, Id debitedAcc, int amount) implements UserEvent {
    @Override
    public Id stateId() {
      return creditedAcc;
    }

    public static CreditRejected of(Id commandId, Id creditedAcc, Id debitedAcc, int amount) {
      return new CreditRejected(Id.of(UUID.randomUUID()), commandId, creditedAcc, debitedAcc, amount);
    }
  }

  record Credited(Id id, Id commandId, Id creditedAcc, Id debitedAcc, int amount) implements UserEvent {
    @Override
    public Id stateId() {
      return creditedAcc;
    }

    public static Credited of(Id commandId, Id creditedAcc, Id debitedAcc, int amount) {
      return new Credited(Id.of(UUID.randomUUID()), commandId, creditedAcc, debitedAcc, amount);
    }
  }

  record Debited(Id id, Id commandId, Id debitedAcc, Id creditedAcc, int amount) implements UserEvent {
    @Override
    public Id stateId() {
      return debitedAcc;
    }

    public static Debited of(Id commandId, Id debitedAcc, Id creditedAcc, int amount) {
      return new Debited(Id.of(UUID.randomUUID()), commandId, debitedAcc, creditedAcc, amount);
    }
  }

  record UserCreated(Id id, Id commandId, Id userId, String name, int balance) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }

    public static UserCreated of(Id commandId, Id userId, String name, int balance) {
      return new UserCreated(Id.of(UUID.randomUUID()), commandId, userId, name, balance);
    }
  }
}
