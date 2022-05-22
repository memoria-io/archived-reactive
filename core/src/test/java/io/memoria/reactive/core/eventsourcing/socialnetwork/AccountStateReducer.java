package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.pipeline.StateReducer;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.Acc;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.AccountCreated;

public record AccountStateReducer() implements StateReducer<Account, AccountEvent> {
  @Override
  public AccountEvent apply(Account account) {
    return switch (account) {
      case Visitor acc -> throw new IllegalArgumentException("State is an initial state");
      case Acc acc -> accountCreated(acc);
      case ClosedAccount acc -> new AccountClosed(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId());
    };
  }

  private AccountCreated accountCreated(Acc acc) {
    return new AccountCreated(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId(), acc.name());
  }
}
