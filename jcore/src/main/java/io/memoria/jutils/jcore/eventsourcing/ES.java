package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("ClassCanBeRecord")
public class ES<S, C extends Command> implements Function1<C, Mono<S>> {
  public static <S> Mono<ConcurrentHashMap<Id, S>> buildState(EventRepo eventRepo, Evolver<S> evolver) {
    var state = new ConcurrentHashMap<Id, S>();
    return eventRepo.find().doOnNext(events -> buildState(state, evolver, events)).then(Mono.just(state));
  }

  private final transient ConcurrentMap<Id, S> state;
  private final transient S defaultState;
  private final transient EventRepo eventRepo;
  private final Decider<S, C> decider;
  private final Evolver<S> evolver;

  public ES(S defaultState,
            ConcurrentMap<Id, S> state,
            EventRepo eventRepo,
            Decider<S, C> decider,
            Evolver<S> evolver) {
    this.state = state;
    this.defaultState = defaultState;
    this.eventRepo = eventRepo;
    this.decider = decider;
    this.evolver = evolver;
  }

  /**
   * @return mono of the new State after applying such command on it.
   */
  public Mono<S> apply(C cmd) {
    return Mono.fromCallable(() -> {
      var currentState = state.getOrDefault(cmd.aggId(), defaultState);
      var events = decider.apply(currentState, cmd).get();
      var add = eventRepo.add(events);
      var persist = Mono.fromCallable(() -> persist(currentState, cmd, events));
      return add.then(persist);
    }).flatMap(Function.identity());
  }

  private S persist(S currentState, C cmd, List<Event> events) {
    var newState = events.foldLeft(currentState, evolver);
    state.put(cmd.aggId(), newState);
    return newState;
  }

  private static <S> void buildState(ConcurrentHashMap<Id, S> stateStore, Evolver<S> evolver, List<Event> events) {
    events.forEach(event -> stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event)));
  }
}
