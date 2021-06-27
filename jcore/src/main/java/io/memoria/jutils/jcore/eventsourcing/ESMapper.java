package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.Function1;
import io.vavr.collection.Map;
import reactor.core.publisher.Mono;

import static io.memoria.jutils.jcore.eventsourcing.ESException.UnknownCommand.create;

public record ESMapper(Map<Class<? extends Command>, ES> handlers) implements Function1<Command, Mono<State>> {
  @Override
  public Mono<State> apply(Command command) {
    return handlers.keySet()
                   .find(key -> command.getClass().isInstance(key))
                   .map(k -> handlers.get(k).get().apply(command))
                   .getOrElse(Mono.error(create(command.getClass().getSimpleName())));
  }
}
