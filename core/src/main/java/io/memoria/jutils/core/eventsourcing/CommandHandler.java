package io.memoria.jutils.core.eventsourcing;

import io.vavr.Function1;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface CommandHandler<S extends State, C extends Command> extends Function1<C, Flux<Event>> {
  default Flux<Event> apply(Flux<C> cmds) {
    return cmds.concatMap(this);
  }
}
