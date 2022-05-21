package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface Account extends State {
  record Acc(StateId stateId, String name) implements Account {
    public Acc withNewName(String newName) {
      return new Acc(stateId, name + ":" + newName);
    }
  }

  record Visitor(StateId stateId) implements Account {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
