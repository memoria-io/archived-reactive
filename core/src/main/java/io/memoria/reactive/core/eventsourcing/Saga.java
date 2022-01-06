package io.memoria.reactive.core.eventsourcing;

import io.vavr.Function1;
import io.vavr.control.Option;

@FunctionalInterface
public interface Saga extends Function1<Event, Option<Event>> {}
