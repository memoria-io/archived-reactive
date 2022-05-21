package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateDecider;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Acc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.ClosedAcc;
import io.memoria.reactive.core.eventsourcing.sharding.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.sharding.AccountCommand.ChangeName;
import io.memoria.reactive.core.eventsourcing.sharding.AccountCommand.CreatePerson;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.AccountClosed;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.sharding.AccountEvent.NameChanged;
import io.vavr.control.Try;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
record AccountStateDecider() implements StateDecider {
  @Override
  public Try<Event> apply(State state, Command command) {
    if (state instanceof Account account && command instanceof AccountCommand accountCommand) {
      return switch (account) {
        case Visitor acc -> handle(acc, accountCommand);
        case Acc acc -> handle(acc, accountCommand);
        case ClosedAcc closedAcc -> handle(closedAcc, accountCommand);
      };
    }
    return Try.failure(UnknownCommand.create(command));
  }

  private Try<Event> handle(ClosedAcc closedAcc, AccountCommand accountCommand) {
    return Try.success(new AccountClosed(EventId.randomUUID(), CommandId.randomUUID(), closedAcc.accountId()));
  }

  private Try<Event> handle(Visitor acc, AccountCommand cmd) {
    return switch (cmd) {
      case CreatePerson c -> accountCreated(c);
      default -> Try.failure(UnknownCommand.create(cmd));
    };
  }

  private Try<Event> handle(Acc acc, AccountCommand cmd) {
    return switch (cmd) {
      case ChangeName c -> nameChanged(c);
      default -> Try.failure(UnknownCommand.create(cmd));
    };
  }

  private Try<Event> accountCreated(CreatePerson c) {
    return Try.success(new AccountCreated(EventId.randomUUID(), c.commandId(), c.accountId(), c.accName()));
  }

  private Try<Event> nameChanged(ChangeName c) {
    return Try.success(new NameChanged(EventId.randomUUID(), c.commandId(), c.accountId(), c.newName()));
  }
}
