package io.memoria.jutils.jcore.eventsourcing.stream;

import io.memoria.jutils.jcore.eventsourcing.Command;
import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.eventsourcing.Decider;
import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.Evolver;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.stream.StreamRepo;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("ClassCanBeRecord")
public class StreamCommandHandler<S, C extends Command> implements CommandHandler<S, C> {

  public static <S, C extends Command> Mono<CommandHandler<S, C>> create(S defaultState,
                                                                         StreamRepo streamRepo,
                                                                         Decider<S, C> decider,
                                                                         Evolver<S> evolver,
                                                                         TextTransformer transformer) {
    var stateStore = new ConcurrentHashMap<Id, S>();
    var handler = new StreamCommandHandler<>(defaultState, stateStore, streamRepo, decider, evolver, transformer);
    return streamRepo.subscribeToLast()
                     .map(eventStr -> transformer.deserialize(eventStr, Event.class))
                     .map(Try::get)
                     .doOnNext(event -> stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event)))
                     .then(Mono.just(handler));
  }

  private final ConcurrentHashMap<Id, S> stateStore;
  private final S defaultState;
  private final StreamRepo streamRepo;
  private final Decider<S, C> decider;
  private final Evolver<S> evolver;
  private final TextTransformer transformer;

  public StreamCommandHandler(S defaultState,
                               ConcurrentHashMap<Id, S> stateStore,
                               StreamRepo streamRepo,
                               Decider<S, C> decider,
                               Evolver<S> evolver,
                               TextTransformer transformer) {
    this.stateStore = stateStore;
    this.defaultState = defaultState;
    this.streamRepo = streamRepo;
    this.decider = decider;
    this.evolver = evolver;
    this.transformer = transformer;
  }

  /**
   * @return mono of the new State after applying such command on it.
   */
  @Override
  public Mono<S> apply(C cmd) {
    return Mono.fromCallable(() -> {
      var currentState = stateStore.getOrDefault(cmd.aggId(), defaultState);
      var events = decider.apply(currentState, cmd).get();
      return publish(events).then(Mono.fromCallable(() -> persist(currentState, cmd, events)));
    }).flatMap(Function.identity());
  }

  private S persist(S currentState, C cmd, List<Event> events) {
    var newState = events.foldLeft(currentState, evolver);
    stateStore.put(cmd.aggId(), newState);
    return newState;
  }

  private Mono<Void> publish(List<Event> events) {
    var msgs = events.map(transformer::serialize).map(Try::get);
    return streamRepo.publish(msgs).then();
  }
}
