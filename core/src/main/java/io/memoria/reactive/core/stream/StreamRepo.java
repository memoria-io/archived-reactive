package io.memoria.reactive.core.stream;

import io.vavr.Function1;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StreamRepo {
  Mono<Integer> create(String topic);

  default Mono<Msg> get(String topic, int index) {
    return subscribe(topic, index).next();
  }

  Mono<Integer> publish(String topic, Msg msg);

  default Function1<Msg, Mono<Integer>> publish(String topic) {
    return (msg) -> publish(topic, msg);
  }

  Mono<Integer> size(String topic);

  Flux<Msg> subscribe(String topic, int skipped);

  default Flux<Msg> subscribe(String topic) {
    return subscribe(topic, 0);
  }
}
