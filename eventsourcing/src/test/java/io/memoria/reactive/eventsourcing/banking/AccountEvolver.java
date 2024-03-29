package io.memoria.reactive.eventsourcing.banking;

import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.reactive.eventsourcing.banking.event.AccountClosed;
import io.memoria.reactive.eventsourcing.banking.event.AccountCreated;
import io.memoria.reactive.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.eventsourcing.banking.event.Credited;
import io.memoria.reactive.eventsourcing.banking.event.DebitConfirmed;
import io.memoria.reactive.eventsourcing.banking.event.Debited;
import io.memoria.reactive.eventsourcing.banking.state.Acc;
import io.memoria.reactive.eventsourcing.banking.state.Account;
import io.memoria.reactive.eventsourcing.banking.state.ClosedAccount;
import io.memoria.reactive.eventsourcing.banking.state.Visitor;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public record AccountEvolver() implements Evolver<Account, AccountEvent> {
  @Override
  public Account apply(Account account, AccountEvent accountEvent) {
    return switch (account) {
      case Visitor acc -> handle(acc, accountEvent);
      case Acc acc -> handle(acc, accountEvent);
      case ClosedAccount acc -> acc;
    };
  }

  private Account handle(Visitor acc, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case AccountCreated e -> new Acc(e.stateId(), e.name(), e.balance(), 0);
      default -> acc;
    };
  }

  private Account handle(Acc acc, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Credited e -> acc.withCredit(e.amount());
      case Debited e -> acc.withDebit(e.amount());
      case DebitConfirmed e -> acc.withDebitConfirmed();
      case AccountClosed e -> new ClosedAccount(e.accountId());
      default -> acc;
    };
  }
}
