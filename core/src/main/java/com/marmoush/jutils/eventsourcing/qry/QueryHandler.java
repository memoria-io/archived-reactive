package com.marmoush.jutils.eventsourcing.qry;

import io.vavr.Function1;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface QueryHandler extends Function1<QueryRequest, Mono<Try<QueryResponse>>> {}