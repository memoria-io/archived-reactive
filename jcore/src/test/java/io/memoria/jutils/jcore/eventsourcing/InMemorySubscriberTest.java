package io.memoria.jutils.jcore.eventsourcing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

class InMemorySubscriberTest {
  private final String topic = "firstTopic";
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Flux<Event>>> store;
  private final EventSubscriber sub;

  InMemorySubscriberTest() {
    store = new ConcurrentHashMap<>();
    store.put(topic, new ConcurrentHashMap<>());
    sub = new InMemorySubscriber(store);
  }

  @Test
  @DisplayName("Subscribed events should be same as in DB")
  void same() {
    // Given
    int partition0 = 0;
    var events = Flux.interval(Duration.ofMillis(1)).take(100).map(i -> (Event) new UserCreated(i, topic));
    store.get(topic).put(partition0, events);
    // When
    var flux = sub.subscribe(topic, partition0, 0);
    // Then
    StepVerifier.create(flux).expectNextCount(100).expectComplete().verify();
  }
}
