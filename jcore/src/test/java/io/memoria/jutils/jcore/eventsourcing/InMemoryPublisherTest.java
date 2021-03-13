package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;

class InMemoryPublisherTest {
  private final String topic = "firstTopic";
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Flux<Event>>> store;
  private final EventPublisher pub;

  InMemoryPublisherTest() {
    store = new ConcurrentHashMap<>();
    pub = new InMemoryPublisher(store);
  }

  @Test
  @DisplayName("Published events should be same as inserted")
  void same() {
    // Given
    var events = List.range(0, 100).map(i -> (Event) new UserCreated(i, topic));
    // When
    var publishedEvents = pub.apply(topic, 0, events).block();
    // Then
    Assertions.assertEquals(events, publishedEvents);
  }

  @Test
  @DisplayName("Partitioning should work as expected")
  void works() {
    // Given
    int partition0 = 0;
    int partition1 = 1;
    var events = List.range(0, 100).map(i -> new UserCreated(i, topic));
    // When
    var actual0 = pub.apply(topic, partition0, UserCreated.createMany(topic, 0)).block();
    var actual1 = pub.apply(topic, partition1, UserCreated.createMany(topic, 1)).block();

    // then
    var expected0 = store.get(topic).get(partition0);
    var expected1 = store.get(topic).get(partition1);
    assert actual0 != null;
    assert actual1 != null;
    StepVerifier.create(expected0).expectNext(actual0.toJavaArray(Event[]::new)).expectComplete().verify();
    StepVerifier.create(expected1).expectNext(actual1.toJavaArray(Event[]::new)).expectComplete().verify();
  }

  @Test
  @DisplayName("Events should be produced in the right order")
  void order() {
    // Given
    int partition0 = 0;
    var events0 = UserCreated.createMany(topic, 0);
    var events1 = UserCreated.createMany(topic, 1);
    // When
    var actual0 = pub.apply(topic, partition0, events0).block();
    var actual1 = pub.apply(topic, partition0, events1).block();
    // Then
    var expected = store.get(topic).get(partition0);
    assert actual0 != null;
    StepVerifier.create(expected)
                .expectNext(actual0.appendAll(actual1).toJavaArray(Event[]::new))
                .expectComplete()
                .verify();
  }
}
