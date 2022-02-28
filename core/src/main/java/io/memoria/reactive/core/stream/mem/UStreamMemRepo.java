package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.UMsg;
import io.memoria.reactive.core.stream.UStreamRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.Map;

public record UStreamMemRepo(Map<String, Many<UMsg>> topicStreams, int batchSize) implements UStreamRepo {

  @Override
  public Mono<String> create(String topic) {
    return Mono.fromCallable(() -> {
      if (topicStreams.get(topic) == null) {
        var flux = Sinks.many().replay().<UMsg>all(batchSize);
        topicStreams.put(topic, flux);
      }
      return topic;
    });
  }

  @Override
  public Mono<Id> publish(String topic, UMsg uMsg) {
    return Mono.fromCallable(() -> this.topicStreams.get(topic).tryEmitNext(uMsg)).thenReturn(uMsg.id());
  }

  @Override
  public Flux<UMsg> subscribe(String topic, long skipped) {
    return topicStreams.get(topic).asFlux().skip(skipped);
  }
}
