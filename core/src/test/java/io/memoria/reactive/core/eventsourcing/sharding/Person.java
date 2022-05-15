package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface Person extends State {
  record Account(Id id, String name) implements Person {
    public Account withNewName(String newName) {
      return new Account(id, name + ":" + newName);
    }
  }

  record Visitor(Id id) implements Person {
    public Visitor() {
      this(Id.of(UUID.randomUUID()));
    }
  }
}
