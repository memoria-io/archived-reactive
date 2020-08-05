package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function2;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface CommandHandler<S extends State, C extends Command, E extends Event> extends Function2<S, C, Flux<E>> {}
