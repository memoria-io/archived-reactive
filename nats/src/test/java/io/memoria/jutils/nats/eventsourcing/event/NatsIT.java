package io.memoria.jutils.nats.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventStoreTests;
import io.memoria.jutils.core.eventsourcing.event.GreetingTransformer;
import io.memoria.jutils.nats.NatsCore;
import io.memoria.jutils.nats.NatsEventStore;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

class NatsIT {
  private final EventStoreTests eventStoreTests;

  public static void main(String[] args) {
    var f = Flux.create(s -> {
      s.next("hello");
      s.next("hello");
      s.complete();
    });
    StepVerifier.create(f).expectNext("hello", "hello").expectComplete().verify();
  }

  NatsIT() throws IOException, InterruptedException {
    var duration = Duration.ofMillis(2000);
    var nc = NatsCore.createConnection("nats://localhost:4222", duration, duration, 1000, duration);
    var eventStore = new NatsEventStore(nc, duration, Schedulers.boundedElastic(), new GreetingTransformer());
    this.eventStoreTests = new EventStoreTests(eventStore);
  }

  @Test
  void run() {
    eventStoreTests.runAll();
  }
}
