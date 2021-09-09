package io.memoria.reactive.core.eventsourcing;

import io.vavr.Function2;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Decider extends Function2<State, Command, Mono<List<Event>>> {}
