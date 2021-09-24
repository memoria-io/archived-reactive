package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Flux;

public interface ES {
  Flux<State> pipeline(State initState, Evolver evolver, Decider decider);
}
