package com.marmoush.jutils.eventsourcing.domain.port;

import com.marmoush.jutils.eventsourcing.domain.entity.Event;
import io.vavr.Function2;

@FunctionalInterface
public interface EventHandler<T, U extends Event> extends Function2<T, U, T> {}
