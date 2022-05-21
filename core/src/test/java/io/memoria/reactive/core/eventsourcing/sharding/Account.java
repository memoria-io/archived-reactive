package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface Account extends State {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  record Acc(StateId accountId, String name) implements Account {

    public Acc withNewName(String newName) {
      return new Acc(accountId, name + ":" + newName);
    }
  }

  record ClosedAcc(StateId accountId) implements Account {}

  record Visitor(StateId accountId) implements Account {

    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
