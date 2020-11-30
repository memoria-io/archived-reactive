package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.state.Evolver;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.memoria.jutils.core.value.Id;
import io.vavr.Function2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toFlux;

public class CommandHandler<S extends State, C extends Command> implements Function2<Id, C, Mono<List<Event>>> {
  private final transient EventStore eventStore;
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;
  private final transient S initialState;

  public CommandHandler(EventStore eventStore, Evolver<S> evolver, Decider<S, C> decider, S initialState) {
    this.eventStore = eventStore;
    this.evolver = evolver;
    this.decider = decider;
    this.initialState = initialState;
  }

  @Override
  public Mono<List<Event>> apply(Id aggId, C cmd) {
    var stateMono = eventStore.get(aggId).map(events -> evolver.apply(initialState, events));
    var eventsFlux = stateMono.flatMapMany(state -> toFlux(decider.apply(state, cmd)));
    return eventsFlux.concatMap(e -> eventStore.add(aggId, e)).collectList();
  }

  public Flux<Event> apply(Id aggId, Flux<C> cmds) {
    return cmds.concatMap(cmd -> apply(aggId, cmd)).concatMap(Flux::fromIterable);
  }
}
