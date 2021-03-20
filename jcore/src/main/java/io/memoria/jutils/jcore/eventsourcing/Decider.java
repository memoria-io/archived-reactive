package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

@FunctionalInterface
public interface Decider<S, C extends Command> extends Function2<S, C, Try<List<Event>>> {}
