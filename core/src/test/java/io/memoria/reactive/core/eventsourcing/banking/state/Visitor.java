package io.memoria.reactive.core.eventsourcing.banking.state;

import io.memoria.reactive.core.eventsourcing.StateId;

public record Visitor(StateId accountId) implements Account {
  public Visitor() {
    this(StateId.randomUUID());
  }
}
