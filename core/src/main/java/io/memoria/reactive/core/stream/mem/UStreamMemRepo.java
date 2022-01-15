package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.UMsg;
import io.memoria.reactive.core.stream.UStreamRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public record UStreamMemRepo(Map<String, Many<UMsg>> topicStreams, Map<String, AtomicInteger> topicSizes, int batchSize)
        implements UStreamRepo {

  @Override
  public Mono<Void> create(String topic) {
    return Mono.fromRunnable(() -> {
      if (topicStreams.get(topic) == null) {
        var flux = Sinks.many().replay().<UMsg>all(batchSize);
        topicStreams.put(topic, flux);
        topicSizes.put(topic, new AtomicInteger(0));
      }
    });
  }

  @Override
  public Mono<Integer> size(String topic) {
    return Mono.fromCallable(() -> topicSizes.getOrDefault(topic, new AtomicInteger(0)).get());
  }

  @Override
  public Mono<UMsg> publish(String topic, UMsg uMsg) {
    return Mono.fromCallable(() -> {
      var topicSize = this.topicSizes.get(topic);
      this.topicStreams.get(topic).tryEmitNext(uMsg);
      topicSize.incrementAndGet();
      return uMsg;
    });
  }

  @Override
  public Flux<UMsg> subscribe(String topic, int skipped) {
    return topicStreams.get(topic).asFlux().skip(skipped);
  }
}
