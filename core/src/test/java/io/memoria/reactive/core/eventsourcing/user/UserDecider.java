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
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

public record UserDecider(AtomicLong id) implements Decider {
  @Override
  public Mono<List<Event>> apply(State state, Command userCommand) {
    if (userCommand instanceof CreateUser cmd) {
      return Mono.just(List.of(new UserCreated(id.getAndIncrement(), cmd.userId(), cmd.username())));
    }
    if (userCommand instanceof SendMessage cmd) {
      return Mono.just(List.of(new MessageSent(id.getAndIncrement(), cmd.userId(), cmd.receiverId(), cmd.message())));
    }
    return Mono.error(UnknownCommand.create(userCommand.getClass().getSimpleName()));
  }
}
