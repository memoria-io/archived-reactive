package io.memoria.reactive.eventsourcing.banking.state;

import io.memoria.reactive.eventsourcing.State;
import io.memoria.reactive.eventsourcing.StateId;

public sealed interface Account extends State permits Acc, ClosedAccount, Visitor {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }
}
