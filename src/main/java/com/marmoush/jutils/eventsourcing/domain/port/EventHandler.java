package com.marmoush.jutils.eventsourcing.domain.port;

import com.marmoush.jutils.eventsourcing.domain.value.Event;
import io.vavr.Function2;

@FunctionalInterface
public interface EventHandler<T, U extends Event> extends Function2<T, U, T> {}
