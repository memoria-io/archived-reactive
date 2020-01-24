package com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

/**
 * Implementation would contain Repo(s) that has a cached state or can recreate the state from scratch by replaying
 * events
 */
public interface CommandService {
  Mono<Try<List<Event>>> write(Command cmdReq);
}
