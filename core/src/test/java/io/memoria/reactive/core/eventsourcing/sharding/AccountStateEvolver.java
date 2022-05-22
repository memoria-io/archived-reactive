package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.pipeline.StateEvolver;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Acc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.ClosedAcc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.NameChanged;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
record AccountStateEvolver() implements StateEvolver<Account, AccountEvent> {
  @Override
  public Account apply(Account account, AccountEvent accountEvent) {
    return switch (account) {
      case Visitor acc -> handle(acc, accountEvent);
      case Acc acc -> handle(acc, accountEvent);
      case ClosedAcc acc -> handle(acc, accountEvent);
    };
  }

  private Account handle(ClosedAcc acc, AccountEvent accountEvent) {
    return new ClosedAcc(acc.accountId());
  }

  private Account handle(Visitor acc, AccountEvent event) {
    return switch (event) {
      case AccountCreated e -> new Acc(acc.stateId(), e.name());
      default -> acc;
    };
  }

  private Account handle(Acc acc, AccountEvent event) {
    return switch (event) {
      case NameChanged e -> acc.withNewName(e.newName());
      default -> acc;
    };
  }
}
