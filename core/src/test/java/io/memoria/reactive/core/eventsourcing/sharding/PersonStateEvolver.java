package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateEvolver;
import io.memoria.reactive.core.eventsourcing.sharding.Person.Account;
import io.memoria.reactive.core.eventsourcing.sharding.Person.Visitor;
import io.memoria.reactive.core.eventsourcing.sharding.PersonEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.sharding.PersonEvent.NameChanged;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
record PersonStateEvolver() implements StateEvolver {
  @Override
  public State apply(State state, Event event) {
    if (state instanceof Person person && event instanceof PersonEvent personEvent) {
      return switch (person) {
        case Visitor acc -> handle(acc, personEvent);
        case Account acc -> handle(acc, personEvent);
      };
    }
    return state;
  }

  private State handle(Visitor acc, PersonEvent event) {
    return switch (event) {
      case AccountCreated e -> new Account(acc.stateId(), e.name());
      default -> acc;
    };
  }

  private State handle(Account acc, PersonEvent event) {
    return switch (event) {
      case NameChanged e -> acc.withNewName(e.newName());
      default -> acc;
    };
  }
}
