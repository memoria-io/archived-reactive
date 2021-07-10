package io.memoria.jutils.jcore.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StreamRepo {
  Mono<Msg> last();

  Mono<Msg> publish(Msg msg);

  Flux<Msg> subscribe(long offset);

  /**
   * Used for initial state building
   *
   * @return Flux which finishes when it meets the result of {@link StreamRepo#last()}
   */
  default Flux<Msg> subscribeToLast() {
    return last().flatMapMany(l -> subscribe(0).takeUntil(e -> e.equals(l)));
  }
}
