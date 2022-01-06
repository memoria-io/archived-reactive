package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.UserCommand.SendMessage;
import io.memoria.reactive.core.eventsourcing.UserEvent.MessageSent;
import io.memoria.reactive.core.eventsourcing.UserEvent.UserCreated;
import io.vavr.control.Try;

public record UserDecider() implements Decider {
  @Override
  public Try<Event> apply(State state, Command userCommand) {
    return switch (userCommand) {
      case CreateUser cmd -> Try.success(new UserCreated(cmd.userId(), cmd.username()));
      case SendMessage cmd -> Try.success(new MessageSent(cmd.from(), cmd.to(), cmd.message()));
      default -> Try.failure(UnknownCommand.create(userCommand.getClass().getSimpleName()));
    };
  }
}
