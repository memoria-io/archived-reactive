package io.memoria.jutils.core.eventsourcing.state;

import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;

public interface StateStore<S extends State> {
  Option<S> get(Id id);

  S save(S state);
}
