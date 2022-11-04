package io.memoria.reactive.eventsourcing.pipeline.state;

import io.memoria.reactive.eventsourcing.Command;
import io.memoria.reactive.eventsourcing.Event;
import io.memoria.reactive.eventsourcing.State;
import io.vavr.Function2;
import io.vavr.control.Try;

@FunctionalInterface
public interface StateDecider<S extends State, C extends Command, E extends Event> extends Function2<S, C, Try<E>> {

}
