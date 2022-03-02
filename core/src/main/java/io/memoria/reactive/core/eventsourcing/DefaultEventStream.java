package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultEventStream(String topic, int partition, Stream stream, TextTransformer transformer)
        implements EventStream {
  public DefaultEventStream {
    if (partition < 0)
      throw new IllegalArgumentException("Partition value can't be less than 0");
  }

  @Override
  public Flux<Id> publish(Flux<Event> events) {
    var msgs = events.concatMap(this::toEventMsg);
    return stream.publish(msgs);
  }

  @Override
  public Mono<Long> size() {
    return stream.size(topic, partition);
  }

  @Override
  public Flux<Event> subscribe(long offset) {
    return stream.subscribe(topic, partition, offset).flatMap(this::toEvent);
  }

  private Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  private Mono<Msg> toEventMsg(Event event) {
    return transformer.serialize(event).map(body -> new Msg(topic, partition, event.id(), body));
  }
}
