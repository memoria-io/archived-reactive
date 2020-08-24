package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.event.Evolver;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.memoria.jutils.core.eventsourcing.event.Event;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;

public interface Decider<S extends State, C extends Command, E extends Event> extends Function2<S, C, Try<List<E>>> {
  static <S extends State, E extends Event, C extends Command> Mono<Void> handle(EventStore<E> store,
                                                                                 Evolver<S, E> evolver,
                                                                                 Decider<S, C, E> decider,
                                                                                 S initialState,
                                                                                 C cmd,
                                                                                 String aggId) {
    var eventFlux = store.stream(aggId);
    var stateMono = evolver.apply(initialState, eventFlux);
    return stateMono.flatMap(state -> toMono(decider.apply(state, cmd))).flatMap(list -> store.add(aggId, list));
  }
}
