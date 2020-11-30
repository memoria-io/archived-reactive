package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.event.InMemoryEventStore;
import io.memoria.jutils.core.eventsourcing.greeting.GreetingEvent;
import io.memoria.jutils.core.value.Id;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static java.time.Duration.ofMillis;

class InMemoryEventStoreTest {
  private static final Id topic = new Id("topic-" + new Random().nextInt(1000));
  private static final int MSG_COUNT = 20;
  private final EventStore eventStore;
  private final Map<Id, List<Event>> db;
  private final Flux<Event> events;
  private final Event[] expectedEvents;

  public InMemoryEventStoreTest() {
    this.db = new HashMap<>();
    this.eventStore = new InMemoryEventStore(db);
    // Given
    events = Flux.interval(ofMillis(1)).map(GreetingEvent::new).map(e -> (Event) e).take(MSG_COUNT);
    expectedEvents = Objects.requireNonNull(events.collectList().block()).toArray(new Event[0]);
  }

  @Test
  void addShouldBeInRightOrder() {
    // When
    var addedEvents = events.concatMap(e -> eventStore.add(topic, e));
    // Then
    StepVerifier.create(addedEvents).expectNext(expectedEvents).expectComplete().verify();
  }

  @Test
  void produceAndConsume() {
    // When
    var addedEvents = events.concatMap(e -> eventStore.add(topic, e));
    var eventsMono = eventStore.get(topic);
    // Then
    StepVerifier.create(addedEvents).expectNext(expectedEvents).expectComplete().verify();
    StepVerifier.create(eventsMono).expectNext(Arrays.asList(expectedEvents)).expectComplete().verify();
  }
}
