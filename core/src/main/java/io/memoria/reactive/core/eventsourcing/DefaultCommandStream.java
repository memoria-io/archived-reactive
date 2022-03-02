package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.UStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultCommandStream(String topic,
                            int nPartitions,
                            int subscriptionPartition,
                            UStreamRepo uStreamRepo,
                            TextTransformer transformer) implements CommandStream {
  public DefaultCommandStream {
    if (nPartitions < 1)
      throw new IllegalArgumentException("Number of partitions can't be less than 1");
    if (subscriptionPartition < 0)
      throw new IllegalArgumentException("Partition value can't be less than 0");
  }

  @Override
  public Mono<Command> publish(Command command) {
    var partition = command.stateId().hashCode() % nPartitions;
    return CommandStream.toUMsg(command, transformer)
                        .flatMap(msg -> uStreamRepo.publish(topic, partition, msg))
                        .thenReturn(command);
  }

  @Override
  public Flux<Command> subscribe(long skipped) {
    return uStreamRepo.subscribe(topic, subscriptionPartition, skipped)
                      .flatMap(msg -> CommandStream.toCommand(msg, transformer));
  }
}
