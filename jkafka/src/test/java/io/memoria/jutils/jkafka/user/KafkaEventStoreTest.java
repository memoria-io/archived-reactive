package io.memoria.jutils.jkafka.user;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jkafka.KafkaConfigs;
import io.memoria.jutils.jkafka.KafkaEventStore;
import io.vavr.collection.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

public class KafkaEventStoreTest {
//  private static final String topic = "topic-" + new Random().nextInt(1000);
  private static final String topic = "topic-49";
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
    this.eventStore = new KafkaEventStore(KafkaConfigs.producerConf,
                                          KafkaConfigs.consumerConf,
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
    // When
//    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
//                .expectNext(firstEvents)
//                .expectComplete()
//                .verify();
//    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
//                .expectNext(firstEvents)
//                .expectComplete()
//                .verify();
    StepVerifier.create(eventStore.publish(topic, SECOND_PARTITION, secondEvents))
                .expectNext(secondEvents)
                .expectComplete()
                .verify();
    // Then
    //    StepVerifier.create(eventStore.subscribe(topic, FIRST_PARTITION, OFFSET))
    //                .expectNext(expectedFirstEvents)
    //                .expectComplete()
    //                .verify();
    //    StepVerifier.create(eventStore.subscribe(topic, SECOND_PARTITION, OFFSET))
    //                .expectNext(expectedSecondEvents)
    //                .expectComplete()
    //                .verify();
  }

  @Test
  void produceAndConsume() {
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
                .expectNext(firstEvents)
                .expectComplete()
                .verify();
    // Then
    StepVerifier.create(eventStore.subscribe(topic, FIRST_PARTITION, OFFSET))
                .expectNext(expectedFirstEvents)
                .expectComplete()
                .verify();
  }

  @Test
  void lastEvent() {
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
                .expectNext(firstEvents)
                .expectComplete()
                .verify();
    // Then
    StepVerifier.create(eventStore.lastEvent(topic, FIRST_PARTITION))
                .expectNext(firstEvents.head())
                .expectComplete()
                .verify();
  }

  
}