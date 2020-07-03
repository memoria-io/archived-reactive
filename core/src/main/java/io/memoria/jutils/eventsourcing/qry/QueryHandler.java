package io.memoria.jutils.eventsourcing.qry;

import io.vavr.Function1;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface QueryHandler extends Function1<QueryRequest, Mono<QueryResponse>> {}