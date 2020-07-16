package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

@FunctionalInterface
public interface CommandHandler<T1, T2 extends Command, R extends Event> extends Function2<T1, T2, Try<List<R>>> {}
