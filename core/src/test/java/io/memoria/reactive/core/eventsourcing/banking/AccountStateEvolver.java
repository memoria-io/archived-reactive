package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.banking.Account.Acc;
import io.memoria.reactive.core.eventsourcing.banking.Account.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.Credited;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.DebitConfirmed;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.Debited;
import io.memoria.reactive.core.eventsourcing.pipeline.StateEvolver;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
record AccountStateEvolver() implements StateEvolver {
  @Override
  public State apply(State state, Event event) {
    if (state instanceof Account account && event instanceof AccountEvent accountEvent) {
      return switch (account) {
        case Visitor acc -> handle(acc, accountEvent);
        case Acc acc -> handle(acc, accountEvent);
        case ClosedAccount acc -> acc;
      };
    }
    return state;
  }

  private State handle(Visitor acc, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case AccountCreated e -> new Acc(e.stateId(), e.name(), e.balance());
      default -> acc;
    };
  }

  private State handle(Acc acc, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Credited e -> acc.withCredit(e.amount());
      case Debited e -> acc.withDebit(e.amount());
      case DebitConfirmed e -> acc.withDebitConfirmed();
      case AccountClosed e -> new ClosedAccount(e.accountId());
      default -> acc;
    };
  }
}
