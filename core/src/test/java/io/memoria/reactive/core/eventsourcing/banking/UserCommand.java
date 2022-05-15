package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface UserCommand extends Command {
  @Override
  default long timestamp() {
    return 0;
  }

  record CloseAccount(Id id, Id userId) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }

    public static CloseAccount of(Id userId) {
      return new CloseAccount(Id.of(UUID.randomUUID()), userId);
    }
  }

  record ConfirmDebit(Id id, Id debitedAcc) implements UserCommand {
    @Override
    public Id stateId() {
      return debitedAcc;
    }

    public static ConfirmDebit of(Id debitedAcc) {
      return new ConfirmDebit(Id.of(UUID.randomUUID()), debitedAcc);
    }
  }

  record CreateUser(Id id, Id userId, String username, int balance) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }

    public static CreateUser of(Id userId, String username, int balance) {
      return new CreateUser(Id.of(UUID.randomUUID()), userId, username, balance);
    }
  }
  
  record Credit(Id id, Id creditedAcc, Id debitedAcc, int amount) implements UserCommand {
    @Override
    public Id stateId() {
      return creditedAcc;
    }

    public static Credit of(Id creditedAcc, Id debitedAcc, int amount) {
      return new Credit(Id.of(UUID.randomUUID()), creditedAcc, debitedAcc, amount);
    }
  }

  record Debit(Id id, Id debitedAcc, Id creditedAcc, int amount) implements UserCommand {
    @Override
    public Id stateId() {
      return debitedAcc;
    }

    public static Debit of(Id debitedAcc, Id creditedAcc, int amount) {
      return new Debit(Id.of(UUID.randomUUID()), debitedAcc, creditedAcc, amount);
    }
  }
}
