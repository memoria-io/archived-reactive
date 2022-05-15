package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface User extends State {
  record Account(Id id, String name, int balance, int debitCount) implements User {
    public Account(Id id, String name, int balance) {
      this(id, name, balance, 0);
    }

    public boolean hasOngoingDebit() {
      return debitCount != 0;
    }

    public Account withCredit(int credit) {
      return new Account(id, name, balance + credit, debitCount);
    }

    public Account withDebit(int debit) {
      return new Account(id, name, balance - debit, debitCount + 1);
    }

    public Account withDebitConfirmed() {
      return new Account(id, name, balance, debitCount - 1);
    }

    public Account withDebitRejected(int returnedDebit) {
      return new Account(id, name, balance + returnedDebit, debitCount - 1);
    }
  }

  record ClosedAccount(Id id) implements User {}

  record Visitor(Id id) implements User {
    public Visitor() {
      this(Id.of(UUID.randomUUID()));
    }
  }
}
