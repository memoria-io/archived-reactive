package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountCommand extends Command {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  @Override
  default long timestamp() {
    return 0;
  }

  record CloseAccount(CommandId commandId, StateId accountId) implements AccountCommand {
    public static CloseAccount of(StateId accountId) {
      return new CloseAccount(CommandId.randomUUID(), accountId);
    }
  }

  record ConfirmDebit(CommandId commandId, StateId debitedAcc) implements AccountCommand {
    @Override
    public StateId accountId() {
      return debitedAcc;
    }

    public static ConfirmDebit of(StateId debitedAcc) {
      return new ConfirmDebit(CommandId.randomUUID(), debitedAcc);
    }
  }

  record CreateAccount(CommandId commandId, StateId accountId, String accountname, int balance)
          implements AccountCommand {
    public static CreateAccount of(StateId accountId, String accountname, int balance) {
      return new CreateAccount(CommandId.randomUUID(), accountId, accountname, balance);
    }
  }

  record Credit(CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount) implements AccountCommand {
    @Override
    public StateId accountId() {
      return creditedAcc;
    }

    public static Credit of(StateId creditedAcc, StateId debitedAcc, int amount) {
      return new Credit(CommandId.randomUUID(), creditedAcc, debitedAcc, amount);
    }
  }

  record Debit(CommandId commandId, StateId debitedAcc, StateId creditedAcc, int amount) implements AccountCommand {
    @Override
    public StateId accountId() {
      return debitedAcc;
    }

    public static Debit of(StateId debitedAcc, StateId creditedAcc, int amount) {
      return new Debit(CommandId.randomUUID(), debitedAcc, creditedAcc, amount);
    }
  }
}
