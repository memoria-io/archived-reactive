package io.memoria.reactive.core.eventsourcing.banking.command;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.StateId;

public sealed interface AccountCommand extends Command permits CloseAccount, ConfirmDebit, CreateAccount, Credit,
                                                               Debit {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  @Override
  default long timestamp() {
    return 0;
  }

}
