package io.memoria.jutils.pulsar.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventStoreTests;
import io.memoria.jutils.core.eventsourcing.event.GreetingTransformer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class PulsarIT {
  private final EventStoreTests eventStoreTests;

  PulsarIT() throws IOException {
    var eventStore = new PulsarEventStore("pulsar://localhost:6650",
                                          "http://localhost:8080",
                                          new GreetingTransformer());
    this.eventStoreTests = new EventStoreTests(eventStore);
  }

  @Test
  void run() {
    eventStoreTests.runAll();
  }
}

