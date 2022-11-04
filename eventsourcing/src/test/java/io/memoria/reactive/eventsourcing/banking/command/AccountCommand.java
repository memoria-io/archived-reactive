package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.reactive.eventsourcing.Command;
import io.memoria.reactive.eventsourcing.StateId;

public sealed interface AccountCommand extends Command
        permits ChangeName, CloseAccount, ConfirmDebit, CreateAccount, Credit, Debit {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  @Override
  default long timestamp() {
    return 0;
  }
}
