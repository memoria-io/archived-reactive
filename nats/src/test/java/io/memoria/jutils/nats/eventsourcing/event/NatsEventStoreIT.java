package io.memoria.jutils.nats.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventStoreTests;
import io.memoria.jutils.core.eventsourcing.usecase.greet.GreetingTransformer;
import io.memoria.jutils.nats.NatsCore;
import io.memoria.jutils.nats.NatsEventStore;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;

class NatsEventStoreIT {

  @Test
  void run() throws IOException, InterruptedException {
    var duration = Duration.ofMillis(2000);
    var nc = NatsCore.createConnection("nats://localhost:4222", duration, duration, 1000, duration);
    var eventStore = new NatsEventStore(nc, duration, Schedulers.boundedElastic(), new GreetingTransformer());
    new EventStoreTests(eventStore).runAll();
  }
}
