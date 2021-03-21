package io.memoria.jutils.jcore.eventsourcing.data.user;

import io.memoria.jutils.jcore.eventsourcing.Decider;
import io.memoria.jutils.jcore.eventsourcing.ESException.UnknownCommand;
import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserCommand.CreateUser;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserEvent.UserCreated;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public record UserDecider(IdGenerator idGen, Supplier<LocalDateTime> timeSupplier)
        implements Decider<User, UserCommand> {
  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    if (userCommand instanceof CreateUser cmd) {
      var id = idGen.get();
      return Try.success(List.of(new UserCreated(id, "name" + id)));
    }
    return Try.failure(UnknownCommand.create("Unknown command"));
  }
}
