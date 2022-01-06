package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.StreamRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public record MemStreamRepo(Map<String, Many<Msg>> topicStreams, Map<String, AtomicInteger> topicSizes, int batchSize)
        implements StreamRepo {

  @Override
  public Mono<Integer> create(String topic) {
    return Mono.fromCallable(() -> {
      if (topicStreams.get(topic) == null) {
        var flux = Sinks.many().replay().<Msg>all(batchSize);
        topicStreams.put(topic, flux);
        topicSizes.put(topic, new AtomicInteger(0));
      }
      return Mono.just(topicSizes.get(topic).get());
    }).flatMap(Function.identity());
  }

  @Override
  public Mono<Integer> publish(String topic, Msg msg) {
    return Mono.fromCallable(() -> {
      var topicSize = this.topicSizes.get(topic);
      if (msg.sKey() == topicSize.get()) {
        this.topicStreams.get(topic).tryEmitNext(msg);
        return topicSize.incrementAndGet();
      } else {
        var errorMsg = "Sequence key: %s doesn't match current index: %s".formatted(msg.sKey(), topicSize.get());
        throw new IllegalArgumentException(errorMsg);
      }
    });
  }

  @Override
  public Mono<Integer> size(String topic) {
    return Mono.fromCallable(() -> topicSizes.getOrDefault(topic, new AtomicInteger(0)).get());
  }

  @Override
  public Flux<Msg> subscribe(String topic, int skipped) {
    return topicStreams.get(topic).asFlux().skip(skipped);
  }
}
