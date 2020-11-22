package io.memoria.jutils.core.eventsourcing.event;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class InMemoryEventHandlerTest {
  private final EventHandlerTests eventHandlerTests;

  InMemoryEventHandlerTest() {
    var eventStore = new InMemoryEventStore(new HashMap<>());
    this.eventHandlerTests = new EventHandlerTests(eventStore);
  }

  @Test
  void run() {
    eventHandlerTests.runAll();
  }
}
