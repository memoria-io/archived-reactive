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
import io.memoria.reactive.core.id.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

public record UserDecider(IdGenerator idGen) implements Decider {
  @Override
  public Try<List<Event>> apply(State state, Command userCommand) {
    if (userCommand instanceof CreateUser cmd) {
      return Try.success(List.of(new UserCreated(idGen.get(), cmd.userId(), cmd.username())));
    }
    if (userCommand instanceof SendMessage cmd) {
      return Try.success(List.of(new MessageSent(idGen.get(), cmd.userId(), cmd.receiverId(), cmd.message())));
    }
    return Try.failure(UnknownCommand.create(userCommand.getClass().getSimpleName()));
  }
}
