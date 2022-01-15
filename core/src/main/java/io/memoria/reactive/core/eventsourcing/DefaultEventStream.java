package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultEventStream(String topic, OStreamRepo oStreamRepo, TextTransformer transformer) implements EventStream {
  @Override
  public Mono<Void> create() {
    return oStreamRepo.create(topic);
  }

  @Override
  public Mono<Integer> size() {
    return oStreamRepo.size(topic);
  }

  @Override
  public Mono<Event> publish(Event event) {
    return size().flatMap(idx -> toEventMsg(idx, event))
                 .flatMap(msg -> oStreamRepo.publish(topic, msg))
                 .thenReturn(event);
  }

  @Override
  public Flux<Event> subscribe(int skipped) {
    return oStreamRepo.subscribe(topic, skipped).flatMap(this::toEvent);
  }

  private Mono<Event> toEvent(OMsg oMsg) {
    return transformer.deserialize(oMsg.value(), Event.class);
  }

  private Mono<OMsg> toEventMsg(int sKey, Event event) {
    return transformer.serialize(event).map(body -> new OMsg(sKey, body));
  }
}
