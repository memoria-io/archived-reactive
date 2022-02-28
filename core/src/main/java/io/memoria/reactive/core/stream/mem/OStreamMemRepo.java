package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public record OStreamMemRepo(Map<String, Many<OMsg>> topicStreams, Map<String, AtomicLong> topicSizes, int batchSize)
        implements OStreamRepo {

  @Override
  public Mono<String> create(String topic) {
    return Mono.fromCallable(() -> createFn(topic));
  }

  @Override
  public Mono<Long> publish(String topic, OMsg oMsg) {
    return Mono.fromCallable(() -> publishFn(topic, oMsg));
  }

  @Override
  public Mono<Long> size(String topic) {
    return Mono.fromCallable(() -> topicSizes.getOrDefault(topic, new AtomicLong(0)).get());
  }

  @Override
  public Flux<OMsg> subscribe(String topic, long skipped) {
    return topicStreams.get(topic).asFlux().skip(skipped);
  }

  private String createFn(String topic) {
    if (topicStreams.get(topic) == null) {
      var flux = Sinks.many().replay().<OMsg>all(batchSize);
      topicStreams.put(topic, flux);
      topicSizes.put(topic, new AtomicLong(0));
    }
    return topic;
  }

  private long publishFn(String topic, OMsg oMsg) {
    var topicSize = topicSizes.get(topic);
    if (topicSize == null)
      throw unknownTopicException(topic);
    if (oMsg.sKey() == topicSize.get()) {
      this.topicStreams.get(topic).tryEmitNext(oMsg);
      return topicSize.getAndIncrement();
    } else {
      throw wrongSequenceKeyException(oMsg, topicSize.get());
    }
  }

  private IllegalArgumentException wrongSequenceKeyException(OMsg oMsg, long currentIdx) {
    var errorMsg = "Sequence key: %s doesn't match current index: %s".formatted(oMsg.sKey(), currentIdx);
    return new IllegalArgumentException(errorMsg);
  }

  private static IllegalArgumentException unknownTopicException(String topic) {
    return new IllegalArgumentException("Unknown topic: " + topic);
  }
}
