package io.memoria.jutils.core.eventsourcing.state;

import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;

public interface StateStore<S extends State> {
  S save(S state);

  Option<S> get(Id id);
}
