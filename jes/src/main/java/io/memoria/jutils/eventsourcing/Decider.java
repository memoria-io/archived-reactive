package io.memoria.jutils.eventsourcing;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

public interface Decider<S, C extends Command> extends BiFunction<S, C, Try<List<Event>>> {}
