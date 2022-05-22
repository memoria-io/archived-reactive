package io.memoria.reactive.core.eventsourcing.pipeline.saga;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.pipeline.saga.SagaDecider;

public record SagaDomain<E extends Event, C extends Command>(Class<E> eventClass,
                                                             Class<C> commandClass,
                                                             SagaDecider<E, C> decider) {}
