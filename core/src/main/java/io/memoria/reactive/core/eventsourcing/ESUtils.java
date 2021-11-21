package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.rsdb.RSDB;
import io.memoria.reactive.core.rsdb.Read;
import io.memoria.reactive.core.rsdb.Sub;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public class ESUtils {
  private static final Logger log = LoggerFactory.getLogger(ESUtils.class.getName());

  private ESUtils() {}

  public static Mono<ConcurrentHashMap<Id, State>> buildState(Read<Event> eventRepo, Evolver evolver) {
    var state = new ConcurrentHashMap<Id, State>();
    return eventRepo.read(0).doOnNext(events -> buildState(state, evolver, events)).then(Mono.just(state));
  }

  public static Flux<State> pipeline(State initState,
                                     RSDB<Event> eventRSDB,
                                     Evolver evolver,
                                     Sub<Command> cmdSub,
                                     int cmdOffset,
                                     Decider decider) {
    return ESUtils.buildState(eventRSDB, evolver)
                  .map(ns -> new EventStore(initState, ns, eventRSDB, decider, evolver))
                  .flatMapMany(eventStore -> cmdSub.subscribe(cmdOffset).flatMap(eventStore))
                  .onErrorContinue(ESException.class, (t, o) -> log.error("An error occurred", t));
  }

  private static void buildState(ConcurrentHashMap<Id, State> stateStore, Evolver evolver, List<Event> events) {
    events.forEach(event -> stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event)));
  }
}
