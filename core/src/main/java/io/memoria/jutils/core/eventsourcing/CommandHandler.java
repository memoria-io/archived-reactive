package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.value.Id;
import io.vavr.Function2;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface CommandHandler<S extends State, C extends Command> extends Function2<Id, C, Flux<Event>> {
  default Flux<Event> apply(Id aggId, Flux<C> cmds) {
    return cmds.concatMap(cmd -> apply(aggId, cmd));
  }
}
