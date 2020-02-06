package com.marmoush.jutils.eventsourcing;

import io.vavr.Function1;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Query extends Function1<QueryRequest, Mono<Try<QueryResponse>>> {}
