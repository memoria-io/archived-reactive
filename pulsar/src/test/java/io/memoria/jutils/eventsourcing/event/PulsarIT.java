package io.memoria.jutils.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.transformer.StringTransformer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;

import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;

class PulsarIT {
  private static final String topic = "topic-" + new Random().nextInt(1000);
  private static final int MSG_COUNT = 3;
  private static final StringTransformer transformer = new GreetingTransformer();

  private final PulsarEventStore pulsarEventStore;
  private final Flux<Event> events;
  private final Event[] expectedEvents;

  PulsarIT() throws PulsarClientException {
    this.pulsarEventStore = new PulsarEventStore("pulsar://localhost:6650", ofMillis(100), transformer);
    // Given
    events = Flux.interval(ofMillis(100)).map(PulsarIT::toGreetingEvent).map(e -> (Event) e).take(MSG_COUNT);
    expectedEvents = requireNonNull(events.collectList().block()).toArray(new Event[0]);
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  void produceAndConsume() {
    // When
    var sentFlux = pulsarEventStore.add(topic, events);
    var receiveFlux = pulsarEventStore.stream(topic).take(MSG_COUNT);
    // Then
    StepVerifier.create(sentFlux).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(receiveFlux).expectNext(expectedEvents).expectComplete().verify();
  }

  private static GreetingEvent toGreetingEvent(long i) {
    return new GreetingEvent(i + "", "name_%s".formatted(i));
  }
}

