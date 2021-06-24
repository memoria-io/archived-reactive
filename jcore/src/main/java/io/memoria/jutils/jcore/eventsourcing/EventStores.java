package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public class EventStores {
  public static <S, C extends Command> Mono<EventStore<S, C>> create(String aggregate,
                                                                     S defaultState,
                                                                     EventRepo eventRepo,
                                                                     Decider<S, C> decider,
                                                                     Evolver<S> evolver) {
    var stateStore = new ConcurrentHashMap<Id, S>();
    var handler = new EventStore<>(defaultState, stateStore, eventRepo, decider, evolver);
    
    return eventRepo.find(aggregate).doOnNext(events -> evolve(stateStore, evolver, events)).then(Mono.just(handler));
  }

  private EventStores() {}

  private static <S> void evolve(ConcurrentHashMap<Id, S> stateStore, Evolver<S> evolver, List<Event> events) {
    events.forEach(event -> stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event)));
  }
}
