package io.memoria.jutils.jcore.msgbus;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgBus {
  Mono<List<String>> publish(List<String> msg);

  Flux<String> subscribe(long offset);
  
  Mono<String> last();
}
