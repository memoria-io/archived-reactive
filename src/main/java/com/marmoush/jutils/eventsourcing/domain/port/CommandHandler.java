package com.marmoush.jutils.eventsourcing.domain.port;

import com.marmoush.jutils.eventsourcing.domain.entity.EventEntity;
import com.marmoush.jutils.eventsourcing.domain.entity.Command;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

@FunctionalInterface
public interface CommandHandler<S, C extends Command, R extends EventEntity> extends Function2<S, C, Try<List<R>>> {}
