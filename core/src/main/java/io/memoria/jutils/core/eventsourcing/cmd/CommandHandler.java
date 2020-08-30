package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.event.Evolver;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function2;
import io.vavr.collection.Traversable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;
import static java.util.function.Function.identity;

public final class CommandHandler<S extends State, E extends Event, C extends Command>
        implements Function2<String, C, Mono<Void>> {
  private final EventStore<E> store;
  private final Evolver<S, E> evolver;
  private final Decider<S, C, E> decider;
  private final S initialState;

  public CommandHandler(EventStore<E> store, Evolver<S, E> evolver, Decider<S, C, E> decider, S initialState) {
    this.store = store;
    this.evolver = evolver;
    this.decider = decider;
    this.initialState = initialState;
  }

  @Override
  public Mono<Void> apply(String aggId, C cmd) {
    var eventFlux = store.stream(aggId);
    var stateMono = evolver.apply(initialState, eventFlux);
    return stateMono.flatMap(state -> toMono(decider.apply(state, cmd))).flatMap(list -> store.add(aggId, list));
  }

  public Mono<Void> apply(String aggId, Flux<C> cmdFlux) {
    return cmdFlux.reduce(Mono.<Void>empty(), (a, b) -> a.then(apply(aggId, b))).flatMap(identity());
  }

  public Mono<Void> apply(String aggId, Traversable<C> t) {
    return t.foldLeft(Mono.<Void>empty(), (a, b) -> a.then(apply(aggId, b)));
  }
}
