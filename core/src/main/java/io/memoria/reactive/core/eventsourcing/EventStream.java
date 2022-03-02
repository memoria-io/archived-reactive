package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream {
  Flux<Id> publish(Flux<Event> events);

  Mono<Long> size();

  Flux<Event> subscribe(long offset);

  /**
   * @param topic
   * @param partition   a specific partition which event stream can only publish/subscribe to.
   * @param stream
   * @param transformer
   * @return a default implementation of the eventStream API
   */
  static EventStream defaultEventStream(String topic, int partition, Stream stream, TextTransformer transformer) {
    return new DefaultEventStream(topic, partition, stream, transformer);
  }
}
