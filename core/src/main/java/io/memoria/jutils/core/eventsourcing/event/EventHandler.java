package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Traversable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface EventHandler<T1 extends State, T2 extends Event<T1>> extends Function2<T1, T2, T1> {
  default T1 apply(T1 state, Traversable<T2> events) {
    return events.foldLeft(state, this);
  }

  default Mono<T1> apply(T1 state, Flux<T2> events) {
    return events.reduce(state, this);
  }

  default Function1<Traversable<T2>, T1> curried(T1 state) {
    return (Traversable<T2> events) -> apply(state, events);
  }
}
