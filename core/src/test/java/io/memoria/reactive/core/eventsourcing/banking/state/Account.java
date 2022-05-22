package io.memoria.reactive.core.eventsourcing.banking.state;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;

public sealed interface Account extends State permits Acc, ClosedAccount, Visitor {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }
}
