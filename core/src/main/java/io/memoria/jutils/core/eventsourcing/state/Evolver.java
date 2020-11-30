package io.memoria.jutils.core.eventsourcing.state;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Traversable;

@FunctionalInterface
public interface Evolver<S extends State> extends Function2<S, Event, S> {

  default S apply(S s, Traversable<Event> e) {
    return e.foldLeft(s, this);
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
