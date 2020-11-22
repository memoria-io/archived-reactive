package io.memoria.jutils.pulsar.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventStoreTests;
import io.memoria.jutils.core.eventsourcing.usecase.greet.GreetingTransformer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;

class PulsarEventStoreIT {

  @Test
  void run() throws PulsarClientException {
    var eventStore = new PulsarEventStore("pulsar://localhost:6650",
                                          "http://localhost:8080",
                                          new GreetingTransformer());
    new EventStoreTests(eventStore).runAll();
  }
}

