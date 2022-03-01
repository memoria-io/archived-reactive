package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.UMsg;
import io.memoria.reactive.core.stream.UStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream {

  Mono<Command> publish(Command command);

  Flux<Command> subscribe(long skipped);

  static CommandStream defaultCommandStream(String topic,
                                            int partition,
                                            UStreamRepo uStreamRepo,
                                            TextTransformer transformer) {
    return new DefaultCommandStream(topic, partition, uStreamRepo, transformer);
  }

  static Mono<Command> toCommand(UMsg uMsg, TextTransformer transformer) {
    return transformer.deserialize(uMsg.value(), Command.class);
  }

  static Mono<UMsg> toUMsg(Command command, TextTransformer transformer) {
    return transformer.serialize(command).map(body -> new UMsg(command.id(), body));
  }
}
