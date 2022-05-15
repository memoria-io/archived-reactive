package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.ConfirmDebit;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.Credit;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.CreditRejected;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.Credited;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.Debited;
import io.memoria.reactive.core.eventsourcing.pipeline.SagaDecider;
import io.vavr.control.Option;

record UserSagaDecider() implements SagaDecider {

  @Override
  public Option<Command> apply(Event event) {
    return event instanceof UserEvent userEvent ? handleUserEvent(userEvent) : Option.none();
  }

  private Option<Command> handleUserEvent(UserEvent userEvent) {
    return switch (userEvent) {
      case Debited e -> Option.some(Credit.of(e.creditedAcc(), e.debitedAcc(), e.amount()));
      case Credited e -> Option.some(ConfirmDebit.of(e.debitedAcc()));
      case CreditRejected e -> Option.some(Credit.of(e.debitedAcc(), e.creditedAcc(), e.amount()));
      case default -> Option.none();
    };
  }
}
