package io.memoria.reactive.eventsourcing.pipeline.saga;

import io.memoria.reactive.eventsourcing.Command;
import io.memoria.reactive.eventsourcing.Event;

public record SagaDomain<E extends Event, C extends Command>(Class<E> eventClass,
                                                             Class<C> commandClass,
                                                             SagaDecider<E, C> decider) {}
