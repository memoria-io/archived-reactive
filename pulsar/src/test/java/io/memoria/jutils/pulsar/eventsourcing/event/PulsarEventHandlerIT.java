package io.memoria.jutils.pulsar.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EventHandlerTests;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.SocialNetworkTransformer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;

class PulsarEventHandlerIT {

  @Test
  void run() throws PulsarClientException {
    var eventStore = new PulsarEventStore("pulsar://localhost:6650",
                                          "http://localhost:8080",
                                          new SocialNetworkTransformer());
    new EventHandlerTests(eventStore).runAll();
  }
}

