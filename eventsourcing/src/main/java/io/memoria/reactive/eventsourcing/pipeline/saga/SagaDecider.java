package io.memoria.reactive.eventsourcing.pipeline.saga;

import io.memoria.reactive.eventsourcing.Command;
import io.memoria.reactive.eventsourcing.Event;
import io.vavr.Function1;
import io.vavr.control.Option;

@FunctionalInterface
public interface SagaDecider<E extends Event, C extends Command> extends Function1<E, Option<C>> {}
