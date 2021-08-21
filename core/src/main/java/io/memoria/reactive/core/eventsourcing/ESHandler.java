package io.memoria.reactive.core.eventsourcing;

import io.vavr.Function1;
import io.vavr.collection.Map;
import reactor.core.publisher.Mono;

import static io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand.create;

public record ESHandler(Map<Class<? extends Command>, EventStore> handlers) implements Function1<Command, Mono<State>> {
  @Override
  public Mono<State> apply(Command command) {
    return handlers.keySet()
                   .find(key -> command.getClass().isInstance(key))
                   .map(k -> handlers.get(k).get().apply(command))
                   .getOrElse(Mono.error(create(command.getClass().getSimpleName())));
  }
}
