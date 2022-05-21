package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateCompactor;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Acc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.AccountCreated;

public record AccountStateCompactor() implements StateCompactor {
  @Override
  public Event apply(State state) {
    if (state instanceof Account account) {
      return switch (account) {
        case Visitor acc -> throw new IllegalArgumentException("State is an initial state");
        case Acc acc -> accountCreated(acc);
      };
    } else {
      throw new IllegalArgumentException("State %s is not instance of user".formatted(state));
    }
  }

  private AccountCreated accountCreated(Acc acc) {
    return new AccountCreated(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId(), acc.name());
  }
}
