package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.Function2;

@FunctionalInterface
public interface Evolver<S> extends Function2<S, Event, S> {}
