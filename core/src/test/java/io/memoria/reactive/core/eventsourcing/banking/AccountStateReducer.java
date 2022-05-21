package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.banking.Account.Acc;
import io.memoria.reactive.core.eventsourcing.banking.Account.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.pipeline.StateReducer;

public record AccountStateReducer() implements StateReducer {
  @Override
  public Event apply(State state) {
    if (state instanceof Account account) {
      return switch (account) {
        case Visitor acc -> throw new IllegalArgumentException("State is an initial state");
        case Acc acc -> accountCreated(acc);
        case ClosedAccount acc -> new AccountClosed(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId());
      };
    } else {
      throw new IllegalArgumentException("State %s is not instance of account".formatted(state));
    }
  }

  private AccountCreated accountCreated(Acc acc) {
    return new AccountCreated(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId(), acc.name(), acc.balance());
  }
}
