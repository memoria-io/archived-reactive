package io.memoria.jutils.core.eventsourcing.stateful;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.Decider;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.eventsourcing.State;
import io.memoria.jutils.core.value.Id;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A blocking State based command handler
 *
 * @param <S>
 * @param <C>
 */
public final class StatefulHandler<S extends State, C extends Command> implements CommandHandler<S, C> {
  // State 
  private final S initialState;
  private final ConcurrentMap<Id, S> db = new ConcurrentHashMap<>();
  // Logic
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;

  public StatefulHandler(S initialState, Evolver<S> evolver, Decider<S, C> decider) {
    this.initialState = initialState;
    this.evolver = evolver;
    this.decider = decider;
  }

  @Override
  public Flux<Event> apply(Id id, C cmd) {
    var state = Option.of(db.get(id)).getOrElse(initialState);
    db.putIfAbsent(id, state);
    var eventsTrial = decider.apply(state, cmd);
    if (eventsTrial.isSuccess()) {
      var events = eventsTrial.get();
      var newState = evolver.apply(state, events);
      if (db.replace(id, state, newState))
        return Flux.fromIterable(events);
      else
        return Flux.error(new IllegalStateException("Couldn't replace old state %s with new state %s ".formatted(state,
                                                                                                                 newState)));
    } else {
      return Flux.error(eventsTrial.getCause());
    }
  }
}
