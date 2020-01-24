package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.qry;

import reactor.core.publisher.Mono;

public interface QueryService {
  Mono<QueryResponse> read(Query query);
}
