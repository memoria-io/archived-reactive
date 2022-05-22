package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;

public record StateDomain<S extends State, E extends Event, C extends Command>(Class<S> stateClass,
                                                                               Class<E> eventClass,
                                                                               Class<C> commandClass,
                                                                               S initState,
                                                                               StateDecider<S, C, E> decider,
                                                                               StateEvolver<S, E> evolver,
                                                                               StateReducer<S, E> reducer) {}
