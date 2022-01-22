package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.IdGenerator;
import io.memoria.reactive.core.stream.UStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream {

  Mono<Void> createTopic();

  static CommandStream defaultCommandStream(String topic,
                                            UStreamRepo uStreamRepo,
                                            IdGenerator idGenerator,
                                            TextTransformer transformer) {
    return new DefaultCommandStream(topic, uStreamRepo, idGenerator, transformer);
  }

  Mono<Command> publish(Command command);

  Flux<Command> subscribe(int skipped);
}
