package io.memoria.reactive.core.eventsourcing;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

@FunctionalInterface
public interface Decider extends Function2<State, Command, Try<List<Event>>> {}
