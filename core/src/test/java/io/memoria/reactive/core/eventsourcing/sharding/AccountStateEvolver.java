package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateEvolver;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Acc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.NameChanged;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
record AccountStateEvolver() implements StateEvolver {
  @Override
  public State apply(State state, Event event) {
    if (state instanceof Account account && event instanceof AccountEvent accountEvent) {
      return switch (account) {
        case Visitor acc -> handle(acc, accountEvent);
        case Acc acc -> handle(acc, accountEvent);
      };
    }
    return state;
  }

  private State handle(Visitor acc, AccountEvent event) {
    return switch (event) {
      case AccountCreated e -> new Acc(acc.stateId(), e.name());
      default -> acc;
    };
  }

  private State handle(Acc acc, AccountEvent event) {
    return switch (event) {
      case NameChanged e -> acc.withNewName(e.newName());
      default -> acc;
    };
  }
}
