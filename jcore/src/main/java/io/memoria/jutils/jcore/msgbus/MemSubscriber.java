package io.memoria.jutils.jcore.msgbus;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

public record MemSubscriber(String topic,
                            int partition,
                            int offset,
                            ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> store)
        implements MsgBusSubscriber {
  @Override
  public Flux<String> subscribe() {
    return Flux.fromIterable(store.get(topic).get(partition).drop(offset));
  }
}
