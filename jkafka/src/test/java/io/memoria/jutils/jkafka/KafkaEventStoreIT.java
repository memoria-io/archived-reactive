package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jkafka.data.user.UserCreated;
import io.memoria.jutils.jkafka.data.user.UserTextTransformer;
import io.vavr.collection.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Random;

import static io.memoria.jutils.jkafka.TestConfigs.consumerConf;
import static io.memoria.jutils.jkafka.TestConfigs.producerConf;

class KafkaEventStoreIT {
  private static final String FIRST_TRANS_ID = "T0";
  private static final int FIRST_PARTITION = 0;
  private static final String SECOND_TRANS_ID = "T1";
  private static final int SECOND_PARTITION = 1;
  private static final int MSG_COUNT = 20;
  private static final long OFFSET = 0;
  private static final EventStore eventStore;
  private static final List<Event> firstEvents;
  private static final Event[] expectedFirstEvents;
  private static final List<Event> secondEvents;
  private static final Event[] expectedSecondEvents;

  static {
    eventStore = new KafkaEventStore(producerConf,
                                     consumerConf,
                                     Duration.ofMillis(2000),
                                     new UserTextTransformer(),
                                     Schedulers.boundedElastic());
    // Given
    firstEvents = List.range(0, MSG_COUNT).map(UserCreated::new).map(e -> (Event) e).take(MSG_COUNT);
    expectedFirstEvents = firstEvents.toJavaArray(Event[]::new);
    secondEvents = List.range(MSG_COUNT, MSG_COUNT * 2).map(UserCreated::new).map(e -> (Event) e).take(MSG_COUNT);
    expectedSecondEvents = secondEvents.toJavaArray(Event[]::new);
  }

  @Test
  void createTopic() {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);

    // When
    StepVerifier.create(eventStore.createTopic(topic, 2, 1)).verifyComplete();
    // Then
    StepVerifier.create(eventStore.exists(topic)).expectNext(true).expectComplete().verify();
    StepVerifier.create(eventStore.nOfPartitions(topic)).expectNext(2).verifyComplete();
  }

  @Test
  void currentOffset() {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);

    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, FIRST_TRANS_ID, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    // Then
    StepVerifier.create(eventStore.currentOffset(topic, FIRST_PARTITION)).expectNext(MSG_COUNT + 1L).verifyComplete();
  }

  @Test
  void dualPublish() {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);
    var pub1 = eventStore.publish(topic, FIRST_PARTITION, FIRST_TRANS_ID, firstEvents);
    var pub2 = eventStore.publish(topic, FIRST_PARTITION, FIRST_TRANS_ID, secondEvents);
    // When
    StepVerifier.create(pub1.zipWith(pub2)).expectNext(Tuples.of(firstEvents, secondEvents)).verifyComplete();
    // Then
    StepVerifier.create(eventStore.subscribe(topic, FIRST_PARTITION, OFFSET).take(MSG_COUNT))
                .expectNext(expectedFirstEvents)
                .verifyComplete();
  }

  @Test
  void lastEvent() {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);

    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, FIRST_TRANS_ID, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    // Then
    StepVerifier.create(eventStore.lastEvent(topic, FIRST_PARTITION)).expectNext(firstEvents.last()).verifyComplete();
  }

  @Test
  @DisplayName("Multiple different partitions")
  void partitions() {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);
    StepVerifier.create(eventStore.createTopic(topic, 2, 1)).verifyComplete();

    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, FIRST_TRANS_ID, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    StepVerifier.create(eventStore.publish(topic, SECOND_PARTITION, SECOND_TRANS_ID, secondEvents))
                .expectNext(secondEvents)
                .verifyComplete();

    // Then
    StepVerifier.create(eventStore.subscribe(topic, FIRST_PARTITION, OFFSET).take(MSG_COUNT))
                .expectNext(expectedFirstEvents)
                .verifyComplete();
    StepVerifier.create(eventStore.subscribe(topic, SECOND_PARTITION, OFFSET).take(MSG_COUNT))
                .expectNext(expectedSecondEvents)
                .verifyComplete();
  }

  @Test
  void publishAndConsume() {
    // Given
    final String topic = "MyTopic-" + new Random().nextInt(1000);
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, FIRST_TRANS_ID, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    // Then
    StepVerifier.create(eventStore.subscribe(topic, FIRST_PARTITION, OFFSET).take(MSG_COUNT))
                .expectNext(expectedFirstEvents)
                .verifyComplete();
  }
}