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

import java.time.Duration;
import java.util.Random;

import static io.memoria.jutils.jkafka.TestConfigs.consumerConf;
import static io.memoria.jutils.jkafka.TestConfigs.producerConf;

public class KafkaEventStoreTest {
  private static final String topic = "topic-" + new Random().nextInt(1000);
  private static final int FIRST_PARTITION = 0;
  private static final int SECOND_PARTITION = 1;
  private static final int MSG_COUNT = 20;
  private static final long OFFSET = 0;
  private final EventStore eventStore;
  private final List<Event> firstEvents;
  private final Event[] expectedFirstEvents;
  private final List<Event> secondEvents;
  private final Event[] expectedSecondEvents;

  public KafkaEventStoreTest() {
    this.eventStore = new KafkaEventStore(producerConf,
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

  //
  //  @Test
  //   void addShouldBeInRightOrder() {
  //    // When
  //    var sentFlux = eventStore.add(topic, events);
  //    // Then
  //    StepVerifier.create(sentFlux).expectNext(expectedEvents).expectComplete().verify();
  //  }
  //
  @Test
  @DisplayName("Multiple different partitions")
  void partitions() {
    StepVerifier.create(eventStore.createTopic(topic, 2, 1)).expectNext(2).verifyComplete();
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    StepVerifier.create(eventStore.publish(topic, SECOND_PARTITION, secondEvents))
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
  void produceAndConsume() {
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    // Then
    StepVerifier.create(eventStore.subscribe(topic, FIRST_PARTITION, OFFSET).take(MSG_COUNT))
                .expectNext(expectedFirstEvents)
                .verifyComplete();
  }

  @Test
  void createTopic() {
    // When
    StepVerifier.create(eventStore.createTopic(topic, 2, 1)).expectNext(2).verifyComplete();
    // Then
    StepVerifier.create(eventStore.exists(topic)).expectNext(true).expectComplete().verify();
    StepVerifier.create(eventStore.nOfPartitions(topic)).expectNext(2).verifyComplete();
  }

  @Test
  void lastEvent() {
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    // Then
    StepVerifier.create(eventStore.lastEvent(topic, FIRST_PARTITION)).expectNext(firstEvents.last()).verifyComplete();
  }

  @Test
  void lastOffset() {
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
                .expectNext(firstEvents)
                .verifyComplete();
    // Then
    StepVerifier.create(eventStore.lastOffset(topic, FIRST_PARTITION)).expectNext(21L).verifyComplete();
  }
}