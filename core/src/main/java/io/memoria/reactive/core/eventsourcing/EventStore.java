package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Write;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("ClassCanBeRecord")
public class EventStore implements Function1<Command, Mono<State>> {

  private final transient ConcurrentMap<Id, State> state;
  private final transient State defaultState;
  private final transient Write<Event> eventRepo;
  private final Decider decider;
  private final Evolver evolver;

  public EventStore(State defaultState,
                    ConcurrentMap<Id, State> state,
                    Write<Event> eventRepo,
                    Decider decider,
                    Evolver evolver) {
    this.state = state;
    this.defaultState = defaultState;
    this.eventRepo = eventRepo;
    this.decider = decider;
    this.evolver = evolver;
  }

  /**
   * @return mono of the new State after applying such command on it.
   */
  public Mono<State> apply(Command cmd) {
    return Mono.fromCallable(() -> {
      var currentState = state.getOrDefault(cmd.aggId(), defaultState);
      var events = decider.apply(currentState, cmd).get();
      var add = eventRepo.write(events);
      var persist = Mono.fromCallable(() -> persist(currentState, cmd, events));
      return add.then(persist);
    }).flatMap(Function.identity());
  }

  private State persist(State currentState, Command cmd, List<Event> events) {
    var newState = events.foldLeft(currentState, evolver);
    state.put(cmd.aggId(), newState);
    return newState;
  }
}