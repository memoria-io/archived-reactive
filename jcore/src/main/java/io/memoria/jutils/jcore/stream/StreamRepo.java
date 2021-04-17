package io.memoria.jutils.jcore.stream;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StreamRepo {
  Mono<String> last();

  Mono<Long> publish(List<String> msg);

  Flux<String> subscribe(long offset);

  /**
   * Used for initial state building
   *
   * @return Flux which finishes when it meets the result of {@link StreamRepo#last()}
   */
  default Flux<String> subscribeToLast() {
    return last().flatMapMany(l -> subscribe(0).takeUntil(e -> e.equals(l)));
  }
}
