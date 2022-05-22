package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.vavr.Function2;
import io.vavr.control.Try;

@FunctionalInterface
public interface StateDecider<S extends State, C extends Command, E extends Event> extends Function2<S, C, Try<E>> {

}
