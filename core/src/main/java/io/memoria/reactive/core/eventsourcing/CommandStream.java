package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.UMsg;
import io.memoria.reactive.core.stream.UStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream {

  Mono<String> createTopic();

  Mono<Command> publish(Command command);

  Flux<Command> subscribe(long skipped);

  static CommandStream defaultCommandStream(String topic, UStreamRepo uStreamRepo, TextTransformer transformer) {
    return new DefaultCommandStream(topic, uStreamRepo, transformer);
  }

  static Mono<Command> toCommand(UMsg uMsg, TextTransformer transformer) {
    return transformer.deserialize(uMsg.value(), Command.class);
  }

  static Mono<UMsg> toUMsg(Command command, TextTransformer transformer) {
    return transformer.serialize(command).map(body -> new UMsg(command.id(), command.stateId(), body));
  }
}
