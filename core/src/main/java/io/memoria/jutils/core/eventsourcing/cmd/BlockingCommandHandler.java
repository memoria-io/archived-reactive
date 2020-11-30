package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.state.BlockingStateStore;
import io.memoria.jutils.core.eventsourcing.state.Evolver;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.memoria.jutils.core.value.Id;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

/**
 * A blocking State based command handler
 *
 * @param <S>
 * @param <C>
 */
public final class BlockingCommandHandler<S extends State, C extends Command>
        implements Function2<Id, C, Try<List<Event>>> {
  private final transient BlockingStateStore<S> blockingStateStore;
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;
  private final transient S initialState;

  public BlockingCommandHandler(BlockingStateStore<S> blockingStateStore,
                                Evolver<S> evolver,
                                Decider<S, C> decider,
                                S initialState) {
    this.blockingStateStore = blockingStateStore;
    this.evolver = evolver;
    this.decider = decider;
    this.initialState = initialState;
  }

  @Override
  public Try<List<Event>> apply(Id aggId, C cmd) {
    var state = blockingStateStore.get(aggId).getOrElse(initialState);
    var eventsTrial = decider.apply(state, cmd);
    if (eventsTrial.isSuccess()) {
      var newState = evolver.apply(state, eventsTrial.get());
      blockingStateStore.save(newState);
    }
    return eventsTrial;
  }

  public Try<List<Event>> apply(Id aggId, Traversable<C> cmds) {
    return Try.of(() -> cmds.flatMap(cmd -> apply(aggId, cmd).get()).toList());
  }
}
