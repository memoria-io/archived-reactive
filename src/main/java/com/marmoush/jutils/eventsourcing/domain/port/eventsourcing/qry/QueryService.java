package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.qry;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;
import reactor.core.publisher.Mono;

public interface QueryService {
  Mono<? extends Event> read(Query query);
}
