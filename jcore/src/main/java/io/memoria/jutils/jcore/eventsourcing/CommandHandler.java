package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;

@SuppressWarnings("ClassCanBeRecord")
public class CommandHandler<S, C extends Command> implements Function1<C, Mono<List<Event>>> {
  public static <S> Mono<ConcurrentHashMap<Id, S>> buildState(Flux<Event> events, Evolver<S> evolver) {
    ConcurrentHashMap<Id, S> db = new ConcurrentHashMap<>();
    return events.map(event -> db.compute(event.aggId(), (k, oldValue) -> evolver.apply(oldValue, event)))
                 .then(Mono.just(db));
  }

  private final ConcurrentHashMap<Id, S> db;
  private final Decider<S, C> decider;
  private final EventStore eventStore;
  private final String topic;
  private final int partition;
  private final Evolver<S> evolver;
  private final S initState;

  public CommandHandler(ConcurrentHashMap<Id, S> db,
                        Decider<S, C> decider,
                        EventStore eventStore,
                        String topic,
                        int partition,
                        Evolver<S> evolver,
                        S initState) {
    this.db = db;
    this.decider = decider;
    this.eventStore = eventStore;
    this.topic = topic;
    this.partition = partition;
    this.evolver = evolver;
    this.initState = initState;
  }

  /**
   * @return mono of events batch that were successfully published after applying the command or empty mono if no
   * aggregate was found
   */
  @Override
  public Mono<List<Event>> apply(C command) {
    return get(command.aggId()).flatMap(s -> handle(s, command));
  }

  private Mono<S> get(Id aggId) {
    return Mono.fromCallable(() -> db.getOrDefault(aggId, initState));
  }

  private Mono<List<Event>> handle(S s, C command) {
    return Mono.fromCallable(() -> toMono(decider.apply(s, command)))
               .flatMap(Function.identity())
               .flatMap(events -> eventStore.publish(topic, partition, events))
               .map(events -> persist(s, command, events));
  }

  private List<Event> persist(S s, C command, List<Event> events) {
    var newState = events.foldLeft(s, evolver);
    db.put(command.aggId(), newState);
    return events;
  }
}
