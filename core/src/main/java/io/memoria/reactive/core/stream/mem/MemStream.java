package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class MemStream implements Stream {
  private final Map<String, Many<Msg>> topicStream;
  private final Map<String, AtomicLong> topicSize;
  private final int batchSize;

  public MemStream(int batchSize) {
    this.topicStream = new HashMap<>();
    this.topicSize = new HashMap<>();
    this.batchSize = batchSize;
  }

  @Override
  public Flux<Msg> publish(Flux<Msg> msgs) {
    return msgs.map(this::publishFn);
  }

  @Override
  public Mono<Long> size(String topic, int partition) {
    return Mono.fromCallable(() -> topicSize(topic));
  }

  @Override
  public Flux<Msg> subscribe(String topic, int partition, long skipped) {
    return Mono.fromCallable(() -> createTopic(topic)).flatMapMany(f -> f.skip(skipped));
  }

  private Flux<Msg> createTopic(String topic) {
    topicStream.computeIfAbsent(topic, k -> Sinks.many().replay().all(batchSize));
    topicSize.computeIfAbsent(topic, k -> new AtomicLong());
    return topicStream.get(topic).asFlux();
  }

  private Msg publishFn(Msg msg) {
    String topic = msg.topic();
    createTopic(topic);
    this.topicStream.get(topic).tryEmitNext(msg);
    this.topicSize.get(topic).getAndIncrement();
    return msg;
  }

  private long topicSize(String topic) {
    return Option.of(topicSize.get(topic)).map(AtomicLong::get).getOrElse(0L);
  }
}
