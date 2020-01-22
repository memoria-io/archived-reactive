package com.marmoush.jutils.domain.port.eventsourcing.qry;

import com.marmoush.jutils.domain.port.eventsourcing.Event;
import reactor.core.publisher.Mono;

public interface QueryService {
  Mono<? extends Event> read(Query query);
}
