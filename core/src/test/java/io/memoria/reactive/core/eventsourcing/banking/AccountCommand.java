package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountCommand extends Command {
  @Override
  default long timestamp() {
    return 0;
  }

  record CloseAccount(CommandId commandId, StateId userId) implements AccountCommand {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static CloseAccount of(StateId userId) {
      return new CloseAccount(CommandId.randomUUID(), userId);
    }
  }

  record ConfirmDebit(CommandId commandId, StateId debitedAcc) implements AccountCommand {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static ConfirmDebit of(StateId debitedAcc) {
      return new ConfirmDebit(CommandId.randomUUID(), debitedAcc);
    }
  }

  record CreateAccount(CommandId commandId, StateId userId, String username, int balance) implements AccountCommand {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static CreateAccount of(StateId userId, String username, int balance) {
      return new CreateAccount(CommandId.randomUUID(), userId, username, balance);
    }
  }

  record Credit(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) implements AccountCommand {
    @Override
    public StateId stateId() {
      return creditedAcc;
    }

    public static Credit of(StateId creditedAcc, StateId debitedAcc, int amount) {
      return new Credit(CommandId.randomUUID(), creditedAcc, debitedAcc, amount);
    }
  }

  record Debit(CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount) implements AccountCommand {
    @Override
    public StateId stateId() {
      return debitedAcc;
    }

    public static Debit of(StateId debitedAcc, StateId creditedAcc, int amount) {
      return new Debit(CommandId.randomUUID(), debitedAcc, creditedAcc, amount);
    }
  }
}
