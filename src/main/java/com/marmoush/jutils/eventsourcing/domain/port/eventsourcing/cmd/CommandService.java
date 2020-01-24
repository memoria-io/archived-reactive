package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface CommandService {
  Mono<Try<List<Event>>> handle(Command cmdReq);

  Try<Void> evolve(Event event);
}
