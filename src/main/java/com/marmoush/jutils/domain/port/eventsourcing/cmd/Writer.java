package com.marmoush.jutils.domain.port.eventsourcing.cmd;

import com.marmoush.jutils.domain.port.eventsourcing.Event;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

public interface Writer {
  Flux<Try<Event>> write(WriteRequest writeRequest);
}
