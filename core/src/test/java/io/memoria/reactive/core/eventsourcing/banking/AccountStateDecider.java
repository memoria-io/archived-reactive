package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Utils;
import io.memoria.reactive.core.eventsourcing.banking.command.AccountCommand;
import io.memoria.reactive.core.eventsourcing.banking.command.ChangeName;
import io.memoria.reactive.core.eventsourcing.banking.command.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.command.ConfirmDebit;
import io.memoria.reactive.core.eventsourcing.banking.command.CreateAccount;
import io.memoria.reactive.core.eventsourcing.banking.command.Credit;
import io.memoria.reactive.core.eventsourcing.banking.command.Debit;
import io.memoria.reactive.core.eventsourcing.banking.event.AccountClosed;
import io.memoria.reactive.core.eventsourcing.banking.event.AccountCreated;
import io.memoria.reactive.core.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.core.eventsourcing.banking.event.ClosureRejected;
import io.memoria.reactive.core.eventsourcing.banking.event.CreditRejected;
import io.memoria.reactive.core.eventsourcing.banking.event.Credited;
import io.memoria.reactive.core.eventsourcing.banking.event.DebitConfirmed;
import io.memoria.reactive.core.eventsourcing.banking.event.Debited;
import io.memoria.reactive.core.eventsourcing.banking.event.NameChanged;
import io.memoria.reactive.core.eventsourcing.banking.state.Acc;
import io.memoria.reactive.core.eventsourcing.banking.state.Account;
import io.memoria.reactive.core.eventsourcing.banking.state.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.state.Visitor;
import io.memoria.reactive.core.eventsourcing.pipeline.state.StateDecider;
import io.vavr.control.Try;

@SuppressWarnings({"SwitchStatementWithTooFewBranches"})
public record AccountStateDecider() implements StateDecider<Account, AccountCommand, AccountEvent> {

  @Override
  public Try<AccountEvent> apply(Account account, AccountCommand accountCommand) {
    return switch (account) {
      case Visitor acc -> handle(acc, accountCommand);
      case Acc acc -> handle(acc, accountCommand);
      case ClosedAccount acc -> handle(acc, accountCommand);
    };
  }

  private Try<AccountEvent> handle(Visitor visitor, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case CreateAccount cmd -> Try.success(AccountCreated.from(cmd));
      case Debit cmd -> Utils.error(visitor, cmd);
      case Credit cmd -> Utils.error(visitor, cmd);
      case CloseAccount cmd -> Utils.error(visitor, cmd);
      case ConfirmDebit cmd -> Utils.error(visitor, cmd);
      case ChangeName cmd -> Utils.error(visitor, cmd);
    };
  }

  private Try<AccountEvent> handle(Acc acc, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case ChangeName cmd -> Try.success(NameChanged.from(cmd));
      case Debit cmd -> Try.success(Debited.from(cmd));
      case Credit cmd -> Try.success(Credited.from(cmd));
      case ConfirmDebit cmd -> Try.success(DebitConfirmed.from(cmd));
      case CloseAccount cmd -> tryToClose(acc, cmd);
      case CreateAccount cmd -> Utils.error(acc, cmd);
    };
  }

  private Try<AccountEvent> handle(ClosedAccount closedAccount, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case Credit cmd -> Try.success(CreditRejected.from(cmd));
      case ConfirmDebit cmd -> Try.success(DebitConfirmed.from(cmd));
      case ChangeName cmd -> Utils.error(closedAccount, cmd);
      case Debit cmd -> Utils.error(closedAccount, cmd);
      case CreateAccount cmd -> Utils.error(closedAccount, cmd);
      case CloseAccount cmd -> Utils.error(closedAccount, cmd);
    };
  }

  private Try<AccountEvent> tryToClose(Acc acc, CloseAccount cmd) {
    if (acc.hasOngoingDebit())
      return Try.success(ClosureRejected.from(cmd));
    return Try.success(AccountClosed.from(cmd));
  }
}
