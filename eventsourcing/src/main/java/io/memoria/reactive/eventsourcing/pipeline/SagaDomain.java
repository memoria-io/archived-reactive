package io.memoria.reactive.eventsourcing.pipeline;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.rule.Saga;

public record SagaDomain<E extends Event, C extends Command>(Class<E> eventClass,
                                                             Class<C> commandClass,
                                                             Saga<E, C> decider) {}
