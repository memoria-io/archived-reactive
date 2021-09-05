package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.db.Read;
import io.memoria.reactive.core.db.Sub;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public class ES {
  public static Mono<ConcurrentHashMap<Id, State>> buildState(Read<Event> eventRepo, Evolver evolver) {
    var state = new ConcurrentHashMap<Id, State>();
    return eventRepo.read(0).doOnNext(events -> buildState(state, evolver, events)).then(Mono.just(state));
  }

  public static Flux<State> pipeline(Sub<Command> cmdStream, int offset, EventStore eventStore) {
    return cmdStream.subscribe(offset).flatMap(eventStore);
  }

  private static void buildState(ConcurrentHashMap<Id, State> stateStore, Evolver evolver, List<Event> events) {
    events.forEach(event -> stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event)));
  }
}
