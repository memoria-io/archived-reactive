package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.memoria.jutils.core.eventsourcing.state.StateStore;
import io.memoria.jutils.core.value.Id;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

public final class CommandHandler<S extends State, C extends Command> implements Function2<Id, C, Try<List<Event>>> {
  private final transient StateStore<S> stateStore;
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;
  private final transient S initialState;

  public CommandHandler(StateStore<S> stateStore, Evolver<S> evolver, Decider<S, C> decider, S initialState) {
    this.stateStore = stateStore;
    this.evolver = evolver;
    this.decider = decider;
    this.initialState = initialState;
  }

  @Override
  public Try<List<Event>> apply(Id aggId, C cmd) {
    var state = stateStore.get(aggId).getOrElse(initialState);
    var eventsTrial = decider.apply(state, cmd);
    return eventsTrial.map(events -> evolver.apply(state, events)).map(stateStore::save).flatMap(s -> eventsTrial);
  }

  public Try<List<Event>> apply(Id aggId, Traversable<C> cmds) {
    return Try.of(() -> cmds.flatMap(cmd -> apply(aggId, cmd).get()).toList());
  }
}
