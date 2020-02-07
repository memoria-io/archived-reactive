package com.marmoush.jutils.eventsourcing;

import com.marmoush.jutils.core.domain.port.DTO;
import io.vavr.Function1;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Query extends DTO, Function1<QueryRequest, Mono<Try<QueryResponse>>> {}
