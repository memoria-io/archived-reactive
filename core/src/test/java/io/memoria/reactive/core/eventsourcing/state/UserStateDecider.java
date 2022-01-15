package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.User.Account;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateInboundMessage;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateOutboundMessage;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.UserEvent.MessageReceived;
import io.memoria.reactive.core.eventsourcing.UserEvent.MessageSent;
import io.memoria.reactive.core.eventsourcing.UserEvent.UserCreated;
import io.vavr.control.Try;

public record UserStateDecider() implements StateDecider {
  @Override
  public Try<Event> apply(State user, Command userCommand) {
    return switch (user) {
      case Visitor u -> handleVisitor(u, userCommand);
      case Account u -> handleAccount(u, userCommand);
      default -> Try.failure(UnknownCommand.create(userCommand.getClass().getSimpleName()));
    };
  }

  private Try<Event> handleAccount(Account account, Command userCommand) {
    return switch (userCommand) {
      case CreateOutboundMessage cmd -> Try.success(new MessageSent(cmd.userId(), cmd.to(), cmd.message()));
      case CreateInboundMessage cmd -> Try.success(new MessageReceived(cmd.userId(), cmd.from(), cmd.message()));
      default -> Try.failure(UnknownCommand.create(userCommand.getClass().getSimpleName()));
    };
  }

  private Try<Event> handleVisitor(Visitor visitor, Command userCommand) {
    if (userCommand instanceof CreateUser cmd) {
      return Try.success(new UserCreated(cmd.userId(), cmd.username()));
    }
    return Try.failure(UnknownCommand.create(userCommand.getClass().getSimpleName()));
  }
}
