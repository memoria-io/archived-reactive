package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function1;

@FunctionalInterface
public interface Event<T extends State> extends Function1<T, T> {
  T apply(T t);
}
