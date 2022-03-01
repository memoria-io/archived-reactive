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
  public Mono<Id> publish(String topic, int partition, UMsg uMsg) {
    return createFn(topic).map(tp -> {
      this.topicStreams.get(topic).tryEmitNext(uMsg);
      return uMsg.id();
    });
  }

  @Override
  public Flux<Id> publish(String topic, int partition, Flux<UMsg> msgs) {
    return createFn(topic).flatMapMany(tp -> msgs).map(msg -> tryEmit(topic, msg));
  }

  @Override
  public Flux<UMsg> subscribe(String topic, int partition, long skipped) {
    return topicStreams.get(topic).asFlux().skip(skipped);
  }

  private Mono<String> createFn(String topic) {
    return Mono.fromCallable(() -> {
      if (topicStreams.get(topic) == null) {
        var flux = Sinks.many().replay().<UMsg>all(batchSize);
        topicStreams.put(topic, flux);
      }
      return topic;
    });
  }

  private Id tryEmit(String topic, UMsg msg) {
    this.topicStreams.get(topic).tryEmitNext(msg);
    return msg.id();
  }
}
