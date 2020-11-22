package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.Random;

import static java.time.Duration.ofMillis;

public class EventStoreTests {
  private static final String topic = "topic-" + new Random().nextInt(1000);
  private static final int MSG_COUNT = 100;
  private final EventStore eventStore;
  private final Flux<Event> events;
  private final Event[] expectedEvents;

  public EventStoreTests(EventStore eventStore) {
    this.eventStore = eventStore;
    // Given
    events = Flux.interval(ofMillis(100)).map(GreetingEvent::new).map(e -> (Event) e).take(MSG_COUNT);
    expectedEvents = Objects.requireNonNull(events.collectList().block()).toArray(new Event[0]);
  }

  public void addShouldBeInRightOrder() {
    // When
    var sentFlux = eventStore.add(topic, events);
    // Then
    StepVerifier.create(sentFlux).expectNext(expectedEvents).expectComplete().verify();
  }

  public void produceAndConsume() {
    // When
    var sentFlux = eventStore.add(topic, events);
    var receiveFlux = eventStore.stream(topic).take(MSG_COUNT);

    // Then
    StepVerifier.create(sentFlux).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(receiveFlux).expectNext(expectedEvents).expectComplete().verify();
  }

  public void runAll() {
    addShouldBeInRightOrder();
    topicExists();
    produceAndConsume();
  }

  public void topicExists() {
    // When
    var sentFlux = eventStore.add(topic, events);
    StepVerifier.create(sentFlux).expectNextCount(MSG_COUNT).expectComplete().verify();
    // Then
    StepVerifier.create(eventStore.exists(topic)).expectNext(true).expectComplete().verify();
    StepVerifier.create(eventStore.exists(topic + "bla")).expectNext(false).expectComplete().verify();
  }
}
