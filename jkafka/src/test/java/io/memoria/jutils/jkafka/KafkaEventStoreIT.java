package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.SerializableTransformer;
import io.memoria.jutils.jkafka.data.user.UserEvent.UserCreated;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.Random;

class KafkaEventStoreIT {
  private final String topic = "users_topic" + new Random().nextInt(1000);
  private final EventStore eventStore = KafkaEventStore.create(Tests.producerConf,
                                                               Tests.consumerConf,
                                                               topic,
                                                               0,
                                                               new SerializableTransformer());

  @Test
  void pubSubLast() {
    // Given
    var msgCount = 100;
    var batches = Flux.range(0, msgCount)
                      .map(i -> List.of((Event) new UserCreated(Id.of("eventId"), Id.of(i), "name" + i)));
    var expectedEvents = Objects.requireNonNull(batches.flatMap(Flux::fromIterable).collectList().block())
                                .toArray(Event[]::new);
    var expectedLastEvent = (Event) new UserCreated(Id.of("eventId"), Id.of(99), "name" + 99);
    // When
    var publishFlux = batches.concatMap(eventStore::publish);
    // Then
    StepVerifier.create(publishFlux).expectNextCount(msgCount).verifyComplete();
    StepVerifier.create(eventStore.subscribe(0).take(msgCount)).expectNext(expectedEvents).verifyComplete();
    StepVerifier.create(eventStore.last()).expectNext(expectedLastEvent).verifyComplete();
  }
}
