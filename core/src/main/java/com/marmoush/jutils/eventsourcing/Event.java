package com.marmoush.jutils.eventsourcing;

import com.marmoush.jutils.core.domain.port.DTO;
import io.vavr.Function1;

@FunctionalInterface
public interface Event<State> extends DTO, Function1<State, State> {
  State apply(State state);
}
