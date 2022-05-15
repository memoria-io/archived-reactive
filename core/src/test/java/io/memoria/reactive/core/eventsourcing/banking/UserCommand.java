package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface UserCommand extends Command {
  @Override
  default long timestamp() {
    return 0;
  }

  record CloseAccount(CommandId id, StateId userId) implements UserCommand {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static CloseAccount of(StateId userId) {
      return new CloseAccount(CommandId.randomUUID(), userId);
    }
  }

  record ConfirmDebit(CommandId id, StateId debitedAcc) implements UserCommand {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static ConfirmDebit of(StateId debitedAcc) {
      return new ConfirmDebit(CommandId.randomUUID(), debitedAcc);
    }
  }

  record CreateUser(CommandId id, StateId userId, String username, int balance) implements UserCommand {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static CreateUser of(StateId userId, String username, int balance) {
      return new CreateUser(CommandId.randomUUID(), userId, username, balance);
    }
  }

  record Credit(CommandId id, StateId creditedAcc, StateId debitedAcc, int amount) implements UserCommand {
    @Override
    public StateId stateId() {
      return creditedAcc;
    }

    public static Credit of(StateId creditedAcc, StateId debitedAcc, int amount) {
      return new Credit(CommandId.randomUUID(), creditedAcc, debitedAcc, amount);
    }
  }

  record Debit(CommandId id, StateId debitedAcc, StateId creditedAcc, int amount) implements UserCommand {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static Debit of(StateId debitedAcc, StateId creditedAcc, int amount) {
      return new Debit(CommandId.randomUUID(), debitedAcc, creditedAcc, amount);
    }
  }
}
