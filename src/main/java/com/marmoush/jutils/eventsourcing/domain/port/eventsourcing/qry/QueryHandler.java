package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.qry;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface QueryHandler {
  Mono<QueryResponse> handle(QueryRequest queryRequest);
}
