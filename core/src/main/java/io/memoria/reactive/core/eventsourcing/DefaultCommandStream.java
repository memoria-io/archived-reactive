package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.UStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultCommandStream(String topic, UStreamRepo uStreamRepo, TextTransformer transformer)
        implements CommandStream {
  @Override
  public Mono<String> createTopic() {
    return uStreamRepo.create(topic);
  }

  @Override
  public Mono<Command> publish(Command command) {
    return CommandStream.toUMsg(command, transformer)
                        .flatMap(msg -> uStreamRepo.publish(topic, msg))
                        .thenReturn(command);
  }

  @Override
  public Flux<Command> subscribe(long skipped) {
    return uStreamRepo.subscribe(topic, skipped).flatMap(msg -> CommandStream.toCommand(msg, transformer));
  }

}
