package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.Function1;
import reactor.core.publisher.Mono;

public interface CommandHandler<S, C extends Command> extends Function1<C, Mono<S>> {
  Mono<Void> buildState();
}
