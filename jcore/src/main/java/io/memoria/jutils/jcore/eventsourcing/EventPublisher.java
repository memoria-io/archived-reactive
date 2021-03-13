package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.Function3;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface EventPublisher extends Function3<String,Integer, List<Event>, Mono<List<Event>>> {}
