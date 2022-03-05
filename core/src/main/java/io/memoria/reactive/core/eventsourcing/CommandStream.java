package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStream {

  Flux<Id> publish(Flux<Command> commands);

  Mono<Long> size();

  Flux<Command> subscribe(long offset);

  /**
   * Command StateId is used to route the command to the relevant partition
   *
   * @param topic
   * @param nPartitions number of partitions
   * @param stream
   * @param transformer
   * @return a default implementation of the CommandStream API
   */
  static CommandStream defaultCommandStream(String topic,
                                            int nPartitions,
                                            int subscriptionPartition,
                                            Stream stream,
                                            TextTransformer transformer) {
    return new DefaultCommandStream(topic, nPartitions, subscriptionPartition, stream, transformer);
  }
}
