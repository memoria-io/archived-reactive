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

public class StreamCommandHandler<S, C extends Command> implements CommandHandler<S, C> {

  private final ConcurrentHashMap<Id, S> stateStore;
  private final S defaultState;
  private final StreamRepo streamRepo;
  private final Decider<S, C> decider;
  private final Evolver<S> evolver;
  private final TextTransformer transformer;

  public StreamCommandHandler(S defaultState,
                              StreamRepo streamRepo,
                              Decider<S, C> decider,
                              Evolver<S> evolver,
                              TextTransformer transformer) {
    this.stateStore = new ConcurrentHashMap<>();
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

  public Mono<Void> buildState() {
    var buildFlux = streamRepo.subscribeToLast().map(this::update);
    return Mono.fromRunnable(stateStore::clear).thenMany(buildFlux).then();
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

  private S update(String eventStr) {
    var event = transformer.deserialize(eventStr, Event.class).get();
    return stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event));
  }
}
