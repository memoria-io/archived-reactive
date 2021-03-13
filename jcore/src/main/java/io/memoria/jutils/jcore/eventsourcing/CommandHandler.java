package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;

public class CommandHandler<S, C extends Command> {
  private final ConcurrentHashMap<Id, S> db;
  private final Decider<S, C> decider;
  private final EventPublisher publisher;
  private final String topic;
  private final int partition;
  private final Evolver<S> evolver;

  public static <S> Mono<ConcurrentHashMap<Id, S>> initialState(EventSubscriber subscriber,
                                                                String topic,
                                                                int partition,
                                                                Evolver<S> evolver) {
    ConcurrentHashMap<Id, S> db = new ConcurrentHashMap<>();
    var initialEvents = subscriber.lastEventPredicate()
                                  .flatMapMany(ep -> subscriber.subscribe(topic, partition, 0).takeUntil(ep));
    return initialEvents.map(event -> db.compute(event.aggId(), (k, oldValue) -> evolver.apply(oldValue, event)))
                        .then(Mono.just(db));
  }

  public CommandHandler(ConcurrentHashMap<Id, S> db,
                        Decider<S, C> decider,
                        EventPublisher publisher,
                        String topic,
                        int partition,
                        Evolver<S> evolver) {
    this.db = db;
    this.decider = decider;
    this.publisher = publisher;
    this.topic = topic;
    this.partition = partition;
    this.evolver = evolver;
  }

  /**
   * @return mono of events batch that were successfully published after applying the command or empty mono if no
   * aggregate was found
   */
  public Mono<List<Event>> handle(C command) {
    return get(command.aggId()).flatMap(s -> handle(s, command));
  }

  private Mono<S> get(Id aggId) {
    return Mono.fromCallable(() -> Optional.of(db.get(aggId))).flatMap(Mono::justOrEmpty);
  }

  private Mono<List<Event>> handle(S s, C command) {
    var pub = publisher.apply(topic, partition);
    return toMono(decider.apply(s, command)).flatMap(pub).map(events -> {
      var newState = events.foldLeft(s, evolver);
      db.put(command.aggId(), newState);
      return events;
    });
  }
}
