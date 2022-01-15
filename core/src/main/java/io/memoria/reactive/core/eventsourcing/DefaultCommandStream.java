package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.id.IdGenerator;
import io.memoria.reactive.core.stream.UMsg;
import io.memoria.reactive.core.stream.UStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultCommandStream(String topic, UStreamRepo uStreamRepo, IdGenerator idGenerator, TextTransformer transformer)
        implements CommandStream {
  @Override
  public Mono<Void> createTopic() {
    return uStreamRepo.create(topic);
  }

  @Override
  public Mono<Command> publish(Command command) {
    return toCommandMsg(idGenerator.get(), command).flatMap(msg -> uStreamRepo.publish(topic, msg)).thenReturn(command);
  }

  @Override
  public Mono<Integer> size() {
    return uStreamRepo.size(topic);
  }

  @Override
  public Flux<Command> subscribe(int skipped) {
    return uStreamRepo.subscribe(topic, skipped).flatMap(this::toCommand);
  }

  private Mono<Command> toCommand(UMsg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  private Mono<UMsg> toCommandMsg(Id id, Command command) {
    return transformer.serialize(command).map(body -> new UMsg(id, body));
  }
}
