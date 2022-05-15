package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateDecider;
import io.memoria.reactive.core.eventsourcing.sharding.Person.Account;
import io.memoria.reactive.core.eventsourcing.sharding.Person.Visitor;
import io.memoria.reactive.core.eventsourcing.sharding.PersonCommand.ChangeName;
import io.memoria.reactive.core.eventsourcing.sharding.PersonCommand.CreatePerson;
import io.memoria.reactive.core.eventsourcing.sharding.PersonEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.sharding.PersonEvent.NameChanged;
import io.vavr.control.Try;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
record PersonStateDecider() implements StateDecider {
  @Override
  public Try<Event> apply(State state, Command command) {
    if (state instanceof Person person && command instanceof PersonCommand personCommand) {
      return switch (person) {
        case Visitor acc -> handle(acc, personCommand);
        case Account acc -> handle(acc, personCommand);
      };
    }
    return Try.failure(UnknownCommand.create(command));
  }

  private Try<Event> handle(Visitor acc, PersonCommand cmd) {
    return switch (cmd) {
      case CreatePerson c -> accountCreated(c);
      default -> Try.failure(UnknownCommand.create(cmd));
    };
  }

  private Try<Event> handle(Account acc, PersonCommand cmd) {
    return switch (cmd) {
      case ChangeName c -> nameChanged(c);
      default -> Try.failure(UnknownCommand.create(cmd));
    };
  }

  private Try<Event> accountCreated(CreatePerson c) {
    return Try.success(new AccountCreated(EventId.randomUUID(), c.id(), c.userId(), c.username()));
  }

  private Try<Event> nameChanged(ChangeName c) {
    return Try.success(new NameChanged(EventId.randomUUID(), c.id(), c.userId(), c.newName()));
  }
}
