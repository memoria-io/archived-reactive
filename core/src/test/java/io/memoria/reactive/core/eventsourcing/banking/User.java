package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface User extends State {
  record Account(StateId stateId, String name, int balance, int debitCount) implements User {
    public Account(StateId stateId, String name, int balance) {
      this(stateId, name, balance, 0);
    }

    public boolean hasOngoingDebit() {
      return debitCount != 0;
    }

    public Account withCredit(int credit) {
      return new Account(stateId, name, balance + credit, debitCount);
    }

    public Account withDebit(int debit) {
      return new Account(stateId, name, balance - debit, debitCount + 1);
    }

    public Account withDebitConfirmed() {
      return new Account(stateId, name, balance, debitCount - 1);
    }

    public Account withDebitRejected(int returnedDebit) {
      return new Account(stateId, name, balance + returnedDebit, debitCount - 1);
    }
  }

  record ClosedAccount(StateId stateId) implements User {}

  record Visitor(StateId stateId) implements User {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
