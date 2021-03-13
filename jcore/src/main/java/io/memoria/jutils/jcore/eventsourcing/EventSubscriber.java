package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public interface EventSubscriber {

  Mono<Boolean> exists(Id aggId);

  Mono<Predicate<Event>> lastEventPredicate();

  <E extends Event> Flux<E> subscribe(Id aggId, long startOffset, Class<E> as);
}
