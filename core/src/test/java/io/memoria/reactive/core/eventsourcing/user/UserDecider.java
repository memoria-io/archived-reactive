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
    return switch (userCommand) {
      case CreateUser cmd -> Mono.just(List.of(new UserCreated(cmd.userId(), cmd.username())));
      case SendMessage cmd -> Mono.just(List.of(new MessageSent(cmd.userId(), cmd.receiverId(), cmd.message())));
      default -> Mono.error(UnknownCommand.create(userCommand.getClass().getSimpleName()));
    };
  }
}
