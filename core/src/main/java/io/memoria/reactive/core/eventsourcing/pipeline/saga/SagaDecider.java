package io.memoria.reactive.core.eventsourcing.pipeline.saga;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.vavr.Function1;
import io.vavr.control.Option;

@FunctionalInterface
public interface SagaDecider<E extends Event, C extends Command> extends Function1<E, Option<C>> {}
