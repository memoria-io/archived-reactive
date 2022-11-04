package io.memoria.reactive.eventsourcing.pipeline.state;

import io.memoria.reactive.eventsourcing.Event;
import io.memoria.reactive.eventsourcing.State;
import io.vavr.Function2;

@FunctionalInterface
public interface StateEvolver<S extends State, E extends Event> extends Function2<S, E, S> {

}
