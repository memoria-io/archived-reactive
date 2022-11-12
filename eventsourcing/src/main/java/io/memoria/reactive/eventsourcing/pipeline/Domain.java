package io.memoria.reactive.eventsourcing.pipeline;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.rule.Reducer;

public record Domain<S extends State, C extends Command, E extends Event>(Class<S> stateClass,
                                                                          Class<C> commandClass,
                                                                          Class<E> eventClass,
                                                                          S initState,
                                                                          Decider<S, C, E> decider,
                                                                          Evolver<S, E> evolver,
                                                                          Reducer<S, E> reducer) {}
