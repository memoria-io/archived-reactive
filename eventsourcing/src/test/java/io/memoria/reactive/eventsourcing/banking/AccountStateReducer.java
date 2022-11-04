package io.memoria.reactive.eventsourcing.banking;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.banking.event.AccountClosed;
import io.memoria.reactive.eventsourcing.banking.event.AccountCreated;
import io.memoria.reactive.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.eventsourcing.banking.state.Acc;
import io.memoria.reactive.eventsourcing.banking.state.Account;
import io.memoria.reactive.eventsourcing.banking.state.ClosedAccount;
import io.memoria.reactive.eventsourcing.banking.state.Visitor;
import io.memoria.reactive.eventsourcing.pipeline.state.StateReducer;

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
    return new AccountCreated(EventId.randomUUID(), CommandId.randomUUID(), acc.stateId(), acc.name(), acc.balance());
  }
}
