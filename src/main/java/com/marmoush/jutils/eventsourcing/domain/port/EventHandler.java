package com.marmoush.jutils.eventsourcing.domain.port;

import com.marmoush.jutils.eventsourcing.domain.entity.EventEntity;
import io.vavr.Function2;

@FunctionalInterface
public interface EventHandler<T, U extends EventEntity> extends Function2<T, U, T> {}
