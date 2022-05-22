package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.Event;

public sealed interface AccountEvent extends Event
        permits AccountClosed, AccountCreated, ClosureRejected, CreditRejected, Credited, DebitConfirmed, Debited {

  @Override
  default long timestamp() {
    return 0;
  }
}
