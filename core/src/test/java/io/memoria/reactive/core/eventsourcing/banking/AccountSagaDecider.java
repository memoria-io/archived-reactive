package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.banking.command.AccountCommand;
import io.memoria.reactive.core.eventsourcing.banking.command.ConfirmDebit;
import io.memoria.reactive.core.eventsourcing.banking.command.Credit;
import io.memoria.reactive.core.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.core.eventsourcing.banking.event.CreditRejected;
import io.memoria.reactive.core.eventsourcing.banking.event.Credited;
import io.memoria.reactive.core.eventsourcing.banking.event.Debited;
import io.memoria.reactive.core.eventsourcing.pipeline.saga.SagaDecider;
import io.vavr.control.Option;

public record AccountSagaDecider() implements SagaDecider<AccountEvent, AccountCommand> {

  @Override
  public Option<AccountCommand> apply(AccountEvent accountEvent) {
    return switch (accountEvent) {
      case Debited e -> Option.some(Credit.of(e.creditedAcc(), e.debitedAcc(), e.amount()));
      case Credited e -> Option.some(ConfirmDebit.of(e.debitedAcc()));
      case CreditRejected e -> Option.some(Credit.of(e.debitedAcc(), e.creditedAcc(), e.amount()));
      case default -> Option.none();
    };
  }
}
