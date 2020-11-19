package io.memoria.jutils.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.nats.client.Connection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;

class NatsIT {
  private static final String topic = "topic-" + new Random().nextInt(1000);
  private static final int MSG_COUNT = 3;

  private final Connection nc;
  private final EventStore eventStore;
  private final Flux<Event> events;
  private final Event[] expectedEvents;

  NatsIT() throws IOException, InterruptedException {
    var duration = Duration.ofMillis(1000);
    nc = NatsUtils.createConnection("nats://localhost:4222", duration, duration, 1000, duration);
    this.eventStore = new NatsEventStore(nc, new GreetingTransformer(), duration, Schedulers.boundedElastic());

    // Given
    events = Flux.interval(ofMillis(100)).map(NatsIT::toGreetingEvent).map(e -> (Event) e).take(MSG_COUNT);
    expectedEvents = requireNonNull(events.collectList().block()).toArray(new Event[0]);
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  void NatsPubSub() {
    var sender = eventStore.add(topic, events);
    var receiver = eventStore.stream(topic);
    StepVerifier.create(sender.zipWith(receiver)).expectNextCount(MSG_COUNT).expectComplete().verify();
  }

  @AfterEach
  void afterEach() throws InterruptedException {
    nc.close();
  }

  private static GreetingEvent toGreetingEvent(long i) {
    return new GreetingEvent(i + "", "name_%s".formatted(i));
  }
}
