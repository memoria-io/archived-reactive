package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.atom.eventsourcing.Event;

public sealed interface AccountEvent extends Event permits AccountClosed,
                                                           AccountCreated,
                                                           ClosureRejected,
                                                           CreditRejected,
                                                           Credited,
                                                           DebitConfirmed,
                                                           Debited,
                                                           NameChanged {

  @Override
  default long timestamp() {
    return 0;
  }
}
