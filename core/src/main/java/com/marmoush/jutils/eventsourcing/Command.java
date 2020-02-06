package com.marmoush.jutils.eventsourcing;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;

@FunctionalInterface
public interface Command<State> extends Function1<State, Try<List<Event<State>>>> {
  Try<List<Event<State>>> apply(State state);
}
