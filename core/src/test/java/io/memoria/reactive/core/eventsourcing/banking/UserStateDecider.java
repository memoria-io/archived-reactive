package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.banking.User.Account;
import io.memoria.reactive.core.eventsourcing.banking.User.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.User.Visitor;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.ConfirmDebit;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.Credit;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.Debit;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.ClosureRejected;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.CreditRejected;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.Credited;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.DebitConfirmed;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.Debited;
import io.memoria.reactive.core.eventsourcing.banking.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.pipeline.StateDecider;
import io.vavr.control.Try;

@SuppressWarnings({"SwitchStatementWithTooFewBranches"})
record UserStateDecider() implements StateDecider {
  @Override
  public Try<Event> apply(State state, Command command) {
    if (state instanceof User user && command instanceof UserCommand userCommand) {
      return switch (user) {
        case Visitor acc -> handle(acc, userCommand);
        case Account acc -> handle(acc, userCommand);
        case ClosedAccount acc -> handle(acc, userCommand);
      };
    }
    return Try.failure(UnknownCommand.create(command));
  }

  private Try<Event> handle(Visitor user, UserCommand userCommand) {
    return switch (userCommand) {
      case CreateUser cmd -> userCreated(cmd);
      default -> Try.failure(InvalidOperation.create(user, userCommand));
    };
  }

  private Try<Event> handle(Account user, UserCommand userCommand) {
    return switch (userCommand) {
      case Debit cmd -> debited(cmd);
      case Credit cmd -> credited(cmd);
      case CloseAccount cmd -> tryToClose(user, cmd);
      case ConfirmDebit cmd -> debitConfirmed(cmd);
      case CreateUser cmd -> Try.failure(InvalidOperation.create(user, cmd));
    };
  }

  private Try<Event> handle(ClosedAccount user, UserCommand userCommand) {
    return switch (userCommand) {
      case Credit cmd -> creditRejected(cmd);
      case ConfirmDebit cmd -> debitConfirmed(cmd);
      default -> Try.failure(InvalidOperation.create(user, userCommand));
    };
  }

  private Try<Event> debitConfirmed(ConfirmDebit cmd) {
    return Try.success(DebitConfirmed.of(cmd.id(), cmd.debitedAcc()));
  }

  private Try<Event> tryToClose(Account user, CloseAccount cmd) {
    if (user.hasOngoingDebit())
      return Try.success(ClosureRejected.of(cmd.id(), cmd.userId()));
    return Try.success(AccountClosed.of(cmd.id(), cmd.userId()));
  }

  private Try<Event> credited(Credit cmd) {
    return Try.success(Credited.of(cmd.id(), cmd.creditedAcc(), cmd.debitedAcc(), cmd.amount()));
  }

  private Try<Event> creditRejected(Credit cmd) {
    return Try.success(CreditRejected.of(cmd.id(), cmd.creditedAcc(), cmd.debitedAcc(), cmd.amount()));
  }

  private Try<Event> debited(Debit cmd) {
    return Try.success(Debited.of(cmd.id(), cmd.debitedAcc(), cmd.creditedAcc(), cmd.amount()));
  }

  private Try<Event> userCreated(CreateUser cmd) {
    return Try.success(UserCreated.of(cmd.id(), cmd.userId(), cmd.username(), cmd.balance()));
  }
}
