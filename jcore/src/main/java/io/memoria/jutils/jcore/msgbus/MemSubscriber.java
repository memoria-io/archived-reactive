package io.memoria.jutils.jcore.msgbus;

import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;

public record MemSubscriber(String topic,
                            int partition,
                            int offset,
                            ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> store)
        implements MsgBusSubscriber {
  @Override
  public Flux<String> subscribe() {
    return toMono(Try.of(() -> store.get(topic).get(partition))).flatMapMany(Flux::fromIterable);
  }
}
