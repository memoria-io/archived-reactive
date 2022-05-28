package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.stream.StreamConfig;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public final class MemStream implements Stream {
  private final Map<String, List<Many<Msg>>> topicStream;
  private final Map<String, List<AtomicLong>> topicSize;

  public MemStream(StreamConfig... streamConfigs) {
    this(io.vavr.collection.List.of(streamConfigs));
  }

  public MemStream(io.vavr.collection.List<StreamConfig> streamConfigs) {
    this.topicStream = new HashMap<>();
    this.topicSize = new HashMap<>();
    for (StreamConfig s : streamConfigs) {
      setup(s.name(), s.totalPartitions(), s.history());
    }
  }

  @Override
  public Flux<Msg> publish(Flux<Msg> msgs) {
    return msgs.map(this::publishFn);
  }

  @Override
  public Mono<Long> size(String topic, int partition) {
    return Mono.fromCallable(() -> tpSize(topic, partition));
  }

  @Override
  public Flux<Msg> subscribe(String topic, int partition, long skipped) {
    return this.topicStream.get(topic).get(partition).asFlux().skip(skipped);
  }

  private void setup(String topic, int nPartitions, int history) {
    var partitions = IntStream.range(0, nPartitions).mapToObj(i -> Sinks.many().replay().<Msg>limit(history)).toList();
    topicStream.put(topic, partitions);
    var partitionSizes = IntStream.range(0, nPartitions).mapToObj(i -> new AtomicLong()).toList();
    topicSize.put(topic, partitionSizes);
  }

  private long tpSize(String topic, int partition) {
    return Option.of(topicSize.get(topic).get(partition)).map(AtomicLong::get).getOrElse(0L);
  }

  private Msg publishFn(Msg msg) {
    String topic = msg.topic();
    int partition = msg.partition();
    this.topicStream.get(topic).get(partition).tryEmitNext(msg);
    this.topicSize.get(topic).get(partition).getAndIncrement();
    return msg;
  }
}
