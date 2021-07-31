package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.repo.EventRepo;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.StreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public class ES {
  public static Mono<ConcurrentHashMap<Id, State>> buildState(EventRepo eventRepo, Evolver evolver) {
    var state = new ConcurrentHashMap<Id, State>();
    return eventRepo.find().doOnNext(events -> buildState(state, evolver, events)).then(Mono.just(state));
  }

  public static Flux<State> pipeline(StreamRepo cmdStream,
                                     long offset,
                                     TextTransformer transformer,
                                     EventStore eventStore) {
    var transform = transformer.deserialize(Command.class);
    return cmdStream.subscribe(offset).map(Msg::body).map(transform).map(Try::get).flatMap(eventStore);
  }

  private static void buildState(ConcurrentHashMap<Id, State> stateStore, Evolver evolver, List<Event> events) {
    events.forEach(event -> stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event)));
  }
}
