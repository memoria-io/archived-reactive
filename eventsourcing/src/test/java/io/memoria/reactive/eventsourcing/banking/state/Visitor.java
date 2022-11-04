package io.memoria.reactive.eventsourcing.banking.state;

import io.memoria.reactive.eventsourcing.StateId;

public record Visitor(StateId accountId) implements Account {
  public Visitor() {
    this(StateId.randomUUID());
  }
}
