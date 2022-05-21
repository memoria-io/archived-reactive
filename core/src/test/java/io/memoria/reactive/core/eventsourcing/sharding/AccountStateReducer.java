package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateReducer;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Acc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.ClosedAcc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.AccountCreated;

public record AccountStateReducer() implements StateReducer {
  @Override
  public Event apply(State state) {
    if (state instanceof Account account) {
      return switch (account) {
        case Visitor acc -> throw new IllegalArgumentException("State is an initial state");
        case Acc acc -> accountCreated(acc);
        case ClosedAcc acc -> accountClosed(acc);
      };
    } else {
      throw new IllegalArgumentException("State %s is not instance of account".formatted(state));
    }
  }

  private AccountClosed accountClosed(ClosedAcc acc) {
    return new AccountClosed(EventId.randomUUID(), CommandId.randomUUID(), acc.accountId());
  }

  private AccountCreated accountCreated(Acc acc) {
    return new AccountCreated(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId(), acc.name());
  }
}
