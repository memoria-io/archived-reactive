package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.vavr.Function1;

@FunctionalInterface
public interface StateReducer<S extends State, E extends Event> extends Function1<S, E> {}
