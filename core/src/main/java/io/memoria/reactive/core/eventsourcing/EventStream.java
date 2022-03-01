package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream {
  Mono<String> createTopic();

  Mono<Event> publish(Event event);

  Mono<Long> size();

  Flux<Event> subscribe(long skipped);

  static EventStream defaultEventStream(String topic, OStreamRepo oStreamRepo, TextTransformer transformer) {
    return new DefaultEventStream(topic, oStreamRepo, transformer);
  }

  static Mono<Event> toEvent(OMsg oMsg, TextTransformer transformer) {
    return transformer.deserialize(oMsg.value(), Event.class);
  }

  static Mono<OMsg> toEventMsg(Event event, TextTransformer transformer) {
    return transformer.serialize(event).map(body -> new OMsg(event.sKey(), event.stateId(), body));
  }
}
