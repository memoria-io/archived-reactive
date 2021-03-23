package io.memoria.jutils.jkafka.data.user;

import io.memoria.jutils.jcore.eventsourcing.Decider;
import io.memoria.jutils.jcore.eventsourcing.ESException.UnknownCommand;
import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.memoria.jutils.jkafka.data.user.UserCommand.CreateUser;
import io.memoria.jutils.jkafka.data.user.UserEvent.UserCreated;
import io.vavr.collection.List;
import io.vavr.control.Try;

public record UserDecider(IdGenerator idGen) implements Decider<User, UserCommand> {
  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    if (userCommand instanceof CreateUser cmd) {
      return Try.success(List.of(new UserCreated(idGen.get(), cmd.userId(), cmd.username())));
    }
    return Try.failure(UnknownCommand.create("Unknown command"));
  }
}
