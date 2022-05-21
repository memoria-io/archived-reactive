package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.ConfirmDebit;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.Credit;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.CreditRejected;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.Credited;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.Debited;
import io.memoria.reactive.core.eventsourcing.pipeline.SagaDecider;
import io.vavr.control.Option;

record AccountSagaDecider() implements SagaDecider {

  @Override
  public Option<Command> apply(Event event) {
    return event instanceof AccountEvent accountEvent ? handleAccountEvent(accountEvent) : Option.none();
  }

  private Option<Command> handleAccountEvent(AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Debited e -> Option.some(Credit.of(e.creditedAcc(), e.debitedAcc(), e.amount()));
      case Credited e -> Option.some(ConfirmDebit.of(e.debitedAcc()));
      case CreditRejected e -> Option.some(Credit.of(e.debitedAcc(), e.creditedAcc(), e.amount()));
      case default -> Option.none();
    };
  }
}
