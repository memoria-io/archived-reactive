package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;

@FunctionalInterface
public interface Command<T extends State, R extends Event<T>> extends Function1<T, Try<List<R>>> {}
