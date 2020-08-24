package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Traversable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Evolver<S extends State, E extends Event> extends Function2<S, E, S> {
  default S apply(S s, Traversable<E> e) {
    return e.foldLeft(s, this);
  }

  default Mono<S> apply(S s, Flux<E> e) {
    return e.reduce(s, this);
  }

  default Function1<Flux<E>, Mono<S>> curriedFlux(S s) {
    return (Flux<E> e) -> apply(s, e);
  }

  default Function1<Traversable<E>, S> curriedTraversable(S s) {
    return (Traversable<E> e) -> apply(s, e);
  }
}
