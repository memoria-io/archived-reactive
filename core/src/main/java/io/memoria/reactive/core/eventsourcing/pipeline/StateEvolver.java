package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.vavr.Function2;

@FunctionalInterface
public interface StateEvolver extends Function2<State, Event, State> {}
