package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface Account extends State {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  record Acc(StateId accountId, String name, int balance, int debitCount) implements Account {
    public Acc(StateId accountId, String name, int balance) {
      this(accountId, name, balance, 0);
    }

    public boolean hasOngoingDebit() {
      return debitCount != 0;
    }

    public Acc withCredit(int credit) {
      return new Acc(accountId, name, balance + credit, debitCount);
    }

    public Acc withDebit(int debit) {
      return new Acc(accountId, name, balance - debit, debitCount + 1);
    }

    public Acc withDebitConfirmed() {
      return new Acc(accountId, name, balance, debitCount - 1);
    }

    public Acc withDebitRejected(int returnedDebit) {
      return new Acc(accountId, name, balance + returnedDebit, debitCount - 1);
    }
  }

  record ClosedAccount(StateId accountId) implements Account {}

  record Visitor(StateId accountId) implements Account {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
