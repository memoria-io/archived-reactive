package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface Person extends State {
  record Account(StateId stateId, String name) implements Person {
    public Account withNewName(String newName) {
      return new Account(stateId, name + ":" + newName);
    }
  }

  record Visitor(StateId stateId) implements Person {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
