package io.memoria.reactive.core.eventsourcing.user;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Decider;
import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.user.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.user.UserCommand.SendMessage;
import io.memoria.reactive.core.eventsourcing.user.UserEvent.MessageSent;
import io.memoria.reactive.core.eventsourcing.user.UserEvent.UserCreated;
import io.vavr.control.Try;

public record UserDecider() implements Decider {
  @Override
  public Try<Event> apply(State state, Command userCommand) {
    return switch (userCommand) {
      case CreateUser cmd -> Try.success(new UserCreated(cmd.userId(), cmd.username()));
      case SendMessage cmd -> Try.success(new MessageSent(cmd.userId(), cmd.receiverId(), cmd.message()));
      default -> Try.failure(UnknownCommand.create(userCommand.getClass().getSimpleName()));
    };
  }
}
