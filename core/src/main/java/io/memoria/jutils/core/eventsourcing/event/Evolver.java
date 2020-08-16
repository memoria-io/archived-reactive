package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Traversable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Evolver<S extends State, E extends Event> extends Function2<S, E, S> {
  default S apply(S state, Traversable<E> e) {
    return e.foldLeft(state, this);
  }

  default Mono<S> apply(S state, Flux<E> e) {
    return e.reduce(state, this);
  }

  default Function1<Flux<E>, Mono<S>> curriedFlux(S state) {
    return (Flux<E> e) -> apply(state, e);
  }

  default Function1<Traversable<E>, S> curriedTraversable(S state) {
    return (Traversable<E> e) -> apply(state, e);
  }
}
