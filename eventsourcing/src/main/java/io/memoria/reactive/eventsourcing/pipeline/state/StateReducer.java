package io.memoria.reactive.eventsourcing.pipeline.state;

import io.memoria.reactive.eventsourcing.Event;
import io.memoria.reactive.eventsourcing.State;
import io.vavr.Function1;

@FunctionalInterface
public interface StateReducer<S extends State, E extends Event> extends Function1<S, E> {}
