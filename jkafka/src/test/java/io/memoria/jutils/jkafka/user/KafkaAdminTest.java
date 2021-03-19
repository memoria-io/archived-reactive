package io.memoria.jutils.jkafka.user;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.eventsourcing.EventStoreAdmin;
import io.memoria.jutils.jkafka.KafkaAdmin;
import io.memoria.jutils.jkafka.KafkaConfigs;
import io.memoria.jutils.jkafka.KafkaEventStore;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

class KafkaAdminTest {
  //  private static final String topic = "topic-" + new Random().nextInt(1000);
  private static final String topic = "topic-49";
  private static final int FIRST_PARTITION = 0;
  private static final int SECOND_PARTITION = 1;
  private static final int MSG_COUNT = 20;
  private static final long OFFSET = 0;
  private final EventStoreAdmin admin;
  private final EventStore eventStore;
  private final List<Event> firstEvents;
  private final Event[] expectedFirstEvents;

  public KafkaAdminTest() {
    this.admin = new KafkaAdmin(KafkaConfigs.producerConf, Duration.ofMillis(2000), Schedulers.boundedElastic());
    this.eventStore = new KafkaEventStore(KafkaConfigs.producerConf,
                                          KafkaConfigs.consumerConf,
                                          Duration.ofMillis(2000),
                                          new UserTextTransformer(),
                                          Schedulers.boundedElastic());
    // Given
    firstEvents = List.range(0, MSG_COUNT).map(UserCreated::new).map(e -> (Event) e).take(MSG_COUNT);
    expectedFirstEvents = firstEvents.toJavaArray(Event[]::new);
  }

  @Test
  void topicExists() {
    // When
    StepVerifier.create(eventStore.publish(topic, FIRST_PARTITION, firstEvents))
                .expectNext(firstEvents)
                .expectComplete()
                .verify();
    // Then
    StepVerifier.create(admin.exists(topic)).expectNext(true).expectComplete().verify();
  }

  @Test
  void createTopic() {
    var topicCreatedMono = admin.createTopic("topic_2", 3, 1);
    StepVerifier.create(topicCreatedMono).expectNext(3).expectComplete().verify();
  }
}
