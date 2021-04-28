package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.stream.StreamRepo;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;

class KafkaStreamIT {
  private final String topic = "users_topic" + new Random().nextInt(1000);
  private final StreamRepo streamRepo = KafkaStream.create(TestConfigs.producerConf,
                                                           TestConfigs.consumerConf,
                                                           topic,
                                                           0);

  @Test
  void pubSubLast() {
    // Given
    var msgCount = 100;
    var batches = Flux.range(0, msgCount).map(i -> List.of("hello_" + i));
    var expectedEvents = List.range(0, msgCount).map(i -> "hello_" + i).toJavaArray(String[]::new);
    var expectedLastEvent = "hello_99";
    // When
    var publishFlux = batches.concatMap(streamRepo::publish);
    // Then
    StepVerifier.create(publishFlux).expectNextCount(msgCount).verifyComplete();
    StepVerifier.create(streamRepo.subscribe(0).take(msgCount)).expectNext(expectedEvents).verifyComplete();
    StepVerifier.create(streamRepo.last()).expectNext(expectedLastEvent).verifyComplete();
  }
}
