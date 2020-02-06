package com.marmoush.jutils.eventsourcing;

import io.vavr.Function1;

@FunctionalInterface
public interface Event<State> extends Function1<State, State> {
  State apply(State state);
}
