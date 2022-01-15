package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public record OStreamMemRepo(Map<String, Many<OMsg>> topicStreams, Map<String, AtomicInteger> topicSizes, int batchSize)
        implements OStreamRepo {

  @Override
  public Mono<Void> create(String topic) {
    return Mono.fromRunnable(() -> {
      if (topicStreams.get(topic) == null) {
        var flux = Sinks.many().replay().<OMsg>all(batchSize);
        topicStreams.put(topic, flux);
        topicSizes.put(topic, new AtomicInteger(0));
      }
    });
  }

  @Override
  public Mono<OMsg> publish(String topic, OMsg oMsg) {
    return Mono.fromCallable(() -> {
      var topicSize = this.topicSizes.get(topic);
      if (oMsg.sKey() == topicSize.get()) {
        this.topicStreams.get(topic).tryEmitNext(oMsg);
        topicSize.incrementAndGet();
        return oMsg;
      } else {
        var errorMsg = "Sequence key: %s doesn't match current index: %s".formatted(oMsg.sKey(), topicSize.get());
        throw new IllegalArgumentException(errorMsg);
      }
    });
  }

  @Override
  public Mono<Integer> size(String topic) {
    return Mono.fromCallable(() -> topicSizes.getOrDefault(topic, new AtomicInteger(0)).get());
  }

  @Override
  public Flux<OMsg> subscribe(String topic, int skipped) {
    return topicStreams.get(topic).asFlux().skip(skipped);
  }
}
