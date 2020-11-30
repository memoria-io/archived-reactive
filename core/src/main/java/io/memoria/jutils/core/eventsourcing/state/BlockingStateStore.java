package io.memoria.jutils.core.eventsourcing.state;

import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;

/**
 * A blocking state store, with most probably in memory implementations
 *
 * @param <S>
 */
public interface BlockingStateStore<S extends State> {
  Option<S> get(Id id);

  S save(S newState);
}
