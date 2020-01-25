package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing;

import io.vavr.Function2;

@FunctionalInterface
public interface StateBuilder<T, U extends Event> extends Function2<T, U, T> {}
