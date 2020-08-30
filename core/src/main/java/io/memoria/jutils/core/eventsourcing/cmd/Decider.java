package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

public interface Decider<S extends State, C extends Command, E extends Event> extends Function2<S, C, Try<List<E>>> {}
