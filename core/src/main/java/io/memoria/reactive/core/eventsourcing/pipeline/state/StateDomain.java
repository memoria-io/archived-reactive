package io.memoria.reactive.core.eventsourcing.pipeline.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;

public record StateDomain<S extends State, C extends Command, E extends Event>(Class<S> stateClass,
                                                                               Class<C> commandClass,
                                                                               Class<E> eventClass,
                                                                               S initState,
                                                                               StateDecider<S, C, E> decider,
                                                                               StateEvolver<S, E> evolver,
                                                                               StateReducer<S, E> reducer) {}
