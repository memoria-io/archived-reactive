package io.memoria.jutils.core.eventsourcing;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Traversable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Evolver<S> extends Function2<S, Event, S> {

  default S apply(S s, Traversable<Event> e) {
    return e.foldLeft(s, this);
  }

  default Mono<S> apply(S s, Flux<Event> e) {
    return e.reduce(s, this);
  }

  default S apply(S s, Iterable<Event> events) {
    var state = s;
    for (Event e : events) {
      state = apply(state, e);
    }
    return state;
  }

  default Function1<Traversable<Event>, S> curriedTraversable(S s) {
    return (Traversable<Event> e) -> apply(s, e);
  }
}
