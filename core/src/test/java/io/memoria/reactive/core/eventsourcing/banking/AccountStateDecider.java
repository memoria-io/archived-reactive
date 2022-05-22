package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.reactive.core.eventsourcing.banking.Account.Acc;
import io.memoria.reactive.core.eventsourcing.banking.Account.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.ConfirmDebit;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.CreateAccount;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.Credit;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.Debit;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.ClosureRejected;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.CreditRejected;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.Credited;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.DebitConfirmed;
import io.memoria.reactive.core.eventsourcing.banking.AccountEvent.Debited;
import io.memoria.reactive.core.eventsourcing.pipeline.StateDecider;
import io.vavr.control.Try;

@SuppressWarnings({"SwitchStatementWithTooFewBranches"})
record AccountStateDecider() implements StateDecider<Account, AccountCommand, AccountEvent> {

  @Override
  public Try<AccountEvent> apply(Account account, AccountCommand accountCommand) {
    return switch (account) {
      case Visitor acc -> handle(acc, accountCommand);
      case Acc acc -> handle(acc, accountCommand);
      case ClosedAccount acc -> handle(acc, accountCommand);
    };
  }

  private Try<AccountEvent> handle(Visitor account, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case CreateAccount cmd -> accountCreated(cmd);
      default -> Try.failure(InvalidOperation.create(account, accountCommand));
    };
  }

  private Try<AccountEvent> handle(Acc acc, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case Debit cmd -> debited(cmd);
      case Credit cmd -> credited(cmd);
      case CloseAccount cmd -> tryToClose(acc, cmd);
      case ConfirmDebit cmd -> debitConfirmed(cmd);
      case CreateAccount cmd -> Try.failure(InvalidOperation.create(acc, cmd));
    };
  }

  private Try<AccountEvent> handle(ClosedAccount account, AccountCommand accountCommand) {
    return switch (accountCommand) {
      case Credit cmd -> creditRejected(cmd);
      case ConfirmDebit cmd -> debitConfirmed(cmd);
      default -> Try.failure(InvalidOperation.create(account, accountCommand));
    };
  }

  private Try<AccountEvent> debitConfirmed(ConfirmDebit cmd) {
    return Try.success(DebitConfirmed.of(cmd.commandId(), cmd.debitedAcc()));
  }

  private Try<AccountEvent> tryToClose(Acc acc, CloseAccount cmd) {
    if (acc.hasOngoingDebit())
      return Try.success(ClosureRejected.of(cmd.commandId(), cmd.accountId()));
    return Try.success(AccountClosed.of(cmd.commandId(), cmd.accountId()));
  }

  private Try<AccountEvent> credited(Credit cmd) {
    return Try.success(Credited.of(cmd.commandId(), cmd.creditedAcc(), cmd.debitedAcc(), cmd.amount()));
  }

  private Try<AccountEvent> creditRejected(Credit cmd) {
    return Try.success(CreditRejected.of(cmd.commandId(), cmd.creditedAcc(), cmd.debitedAcc(), cmd.amount()));
  }

  private Try<AccountEvent> debited(Debit cmd) {
    return Try.success(Debited.of(cmd.commandId(), cmd.debitedAcc(), cmd.creditedAcc(), cmd.amount()));
  }

  private Try<AccountEvent> accountCreated(CreateAccount cmd) {
    return Try.success(AccountCreated.of(cmd.commandId(), cmd.accountId(), cmd.accountname(), cmd.balance()));
  }
}
