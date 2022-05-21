package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface Account extends State {
  record Acc(StateId stateId, String name, int balance, int debitCount)
          implements io.memoria.reactive.core.eventsourcing.banking.Account {
    public Acc(StateId stateId, String name, int balance) {
      this(stateId, name, balance, 0);
    }

    public boolean hasOngoingDebit() {
      return debitCount != 0;
    }

    public Acc withCredit(int credit) {
      return new Acc(stateId, name, balance + credit, debitCount);
    }

    public Acc withDebit(int debit) {
      return new Acc(stateId, name, balance - debit, debitCount + 1);
    }

    public Acc withDebitConfirmed() {
      return new Acc(stateId, name, balance, debitCount - 1);
    }

    public Acc withDebitRejected(int returnedDebit) {
      return new Acc(stateId, name, balance + returnedDebit, debitCount - 1);
    }
  }

  record ClosedAccount(StateId stateId) implements io.memoria.reactive.core.eventsourcing.banking.Account {}

  record Visitor(StateId stateId) implements io.memoria.reactive.core.eventsourcing.banking.Account {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
