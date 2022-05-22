package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.vavr.Function2;

@FunctionalInterface
public interface StateEvolver<S extends State, E extends Event> extends Function2<S, E, S> {

}
