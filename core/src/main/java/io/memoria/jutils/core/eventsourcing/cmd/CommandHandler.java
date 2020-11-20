package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.ESException;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.event.Evolver;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function2;
import io.vavr.collection.Traversable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toFlux;
import static java.util.function.Function.identity;

public final class CommandHandler<S extends State, C extends Command> implements Function2<String, C, Mono<Void>> {
  private final transient EventStore store;
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;
  private final transient S initialState;

  public CommandHandler(EventStore store, Evolver<S> evolver, Decider<S, C> decider, S initialState) {
    this.store = store;
    this.evolver = evolver;
    this.decider = decider;
    this.initialState = initialState;
  }

  @Override
  public Mono<Void> apply(String aggId, C cmd) {
    if (aggId == null || aggId.isEmpty())
      return Mono.error(ESException.create("Aggregate Id is null or empty"));
    var eventFlux = store.stream(aggId);
    var stateMono = evolver.apply(initialState, eventFlux);
    var newEventsFlux = stateMono.flatMapMany(s -> toFlux(decider.apply(s, cmd)));
    return store.add(aggId, newEventsFlux).then();
  }

  public Mono<Void> apply(String aggId, Flux<C> cmdFlux) {
    return cmdFlux.reduce(Mono.<Void>empty(), (a, b) -> a.then(apply(aggId, b))).flatMap(identity());
  }

  public Mono<Void> apply(String aggId, Traversable<C> t) {
    return t.foldLeft(Mono.empty(), (a, b) -> a.then(apply(aggId, b)));
  }
}
