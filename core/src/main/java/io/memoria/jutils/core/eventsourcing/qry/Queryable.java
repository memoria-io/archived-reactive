package io.memoria.jutils.core.eventsourcing.qry;

import io.vavr.Function1;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Queryable extends Function1<QueryRequest, Mono<QueryResponse>> {}
