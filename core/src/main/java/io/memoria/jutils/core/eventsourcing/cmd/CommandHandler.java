package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function2;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface CommandHandler<T1 extends State, T2 extends Command, R extends Event>
        extends Function2<T1, T2, Flux<R>> {}
