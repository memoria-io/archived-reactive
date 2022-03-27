package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.vavr.Function1;
import io.vavr.control.Option;

@FunctionalInterface
public interface SagaDecider extends Function1<Event, Option<Command>> {}
