package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultCommandStream(String topic,
                            int nPartitions,
                            int subscriptionPartition,
                            Stream stream,
                            TextTransformer transformer) implements CommandStream {
  public DefaultCommandStream {
    if (nPartitions < 1)
      throw new IllegalArgumentException("Number of partitions can't be less than 1");
    if (subscriptionPartition < 0)
      throw new IllegalArgumentException("Partition value can't be less than 0");
  }

  @Override
  public Flux<Id> publish(Flux<Command> commands) {
    var msgs = commands.concatMap(this::toMsg);
    return stream.publish(msgs);
  }

  @Override
  public Flux<Command> subscribe(long offset) {
    return stream.subscribe(topic, subscriptionPartition, offset).flatMap(this::toCommand);
  }

  private Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  private Mono<Msg> toMsg(Command command) {
    var partition = Math.abs(command.stateId().hashCode()) % nPartitions;
    return transformer.serialize(command).map(body -> new Msg(topic, partition, command.id(), body));
  }
}
