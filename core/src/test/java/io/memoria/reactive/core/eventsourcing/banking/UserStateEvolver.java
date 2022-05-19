package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.banking.User.Account;
import io.memoria.reactive.core.eventsourcing.banking.User.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.User.Visitor;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.Credited;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.DebitConfirmed;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.Debited;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.pipeline.StateEvolver;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
record UserStateEvolver() implements StateEvolver {
  @Override
  public State apply(State state, Event event) {
    if (state instanceof User user && event instanceof UserEvent userEvent) {
      return switch (user) {
        case Visitor acc -> handle(acc, userEvent);
        case Account acc -> handle(acc, userEvent);
        case ClosedAccount acc -> acc;
      };
    }
    return state;
  }

  private State handle(Visitor acc, UserEvent userEvent) {
    return switch (userEvent) {
      case UserCreated e -> new Account(e.stateId(), e.name(), e.balance());
      default -> acc;
    };
  }

  private State handle(Account acc, UserEvent userEvent) {
    return switch (userEvent) {
      case Credited e -> acc.withCredit(e.amount());
      case Debited e -> acc.withDebit(e.amount());
      case DebitConfirmed e -> acc.withDebitConfirmed();
      case AccountClosed e -> new ClosedAccount(e.userId());
      default -> acc;
    };
  }
}
