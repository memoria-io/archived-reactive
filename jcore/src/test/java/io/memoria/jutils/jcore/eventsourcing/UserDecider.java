package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.ESException.UnknownCommand;
import io.memoria.jutils.jcore.eventsourcing.UserCommand.CreateUser;
import io.memoria.jutils.jcore.id.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.time.LocalDateTime;
import java.util.function.Supplier;

record UserDecider(IdGenerator idGen, Supplier<LocalDateTime> timeSupplier) implements Decider<User, UserCommand> {
  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    if (userCommand instanceof CreateUser cmd) {
      return Try.success(List.of(new UserCreated(idGen.get(), cmd.aggId(), cmd.username(), timeSupplier.get())));
    }
    return Try.failure(UnknownCommand.create("Unknown command"));
  }
}
