package io.memoria.reactive.eventsourcing.banking.state;

import io.memoria.atom.eventsourcing.StateId;

public record Acc(StateId accountId, String name, int balance, int debitCount) implements Account {
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

  public Acc withName(String newName) {
    return new Acc(accountId, newName, balance, debitCount);
  }
}
