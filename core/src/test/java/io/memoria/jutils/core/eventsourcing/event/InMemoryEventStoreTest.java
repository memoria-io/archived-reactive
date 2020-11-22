package io.memoria.jutils.core.eventsourcing.event;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class InMemoryEventStoreTest {

  @Test
  void run() {
    new EventStoreTests(new InMemoryEventStore(new HashMap<>())).runAll();
  }
}
