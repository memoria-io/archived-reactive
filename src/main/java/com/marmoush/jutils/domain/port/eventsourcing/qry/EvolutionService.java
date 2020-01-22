package com.marmoush.jutils.domain.port.eventsourcing.qry;

import com.marmoush.jutils.domain.port.eventsourcing.Event;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

/**
 * Implementation would contain @{@link com.marmoush.jutils.domain.port.crud.EntityWriteRepo}(s) inside
 */
public interface EvolutionService {
  Mono<Try<Void>> evolve(Event event);
}
