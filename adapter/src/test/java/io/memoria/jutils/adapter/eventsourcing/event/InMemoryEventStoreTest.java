package io.memoria.jutils.adapter.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryEventStoreTest {
  private static record GreetingCreated(String id, String aggId, String value) implements Event {}

  private final Map<String, ArrayList<GreetingCreated>> db = new HashMap<>();
  private final EventStore<GreetingCreated> store = new InMemoryEventStore<>(db);
  private final GreetingCreated e1 = new GreetingCreated("0", "0", "hello");
  private final GreetingCreated e2 = new GreetingCreated("1", "1", "Bye");
  private final GreetingCreated e3 = new GreetingCreated("2", "1", "Ciao");

  @Test
  void add() {
    store.add("1", e1).block();
    store.add("2", e2).block();
    assertNull(db.get("0"));
    assertEquals(e1, db.get("1").get(0));
    assertEquals(e2, db.get("2").get(0));
  }

  @Test
  void addMany() {
    var f = List.of(e1, e2, e3);
    store.add("0", f).block();
    StepVerifier.create(store.stream("0")).expectNext(e1, e2, e3).expectComplete().verify();
  }

  @BeforeEach
  void beforeEach() {
    db.clear();
  }

  @Test
  void exists() {
    store.add("1", e1).block();
    assertEquals(true, store.exists("1").block());
  }

  @Test
  void stream() {
    store.add("0", e1).block();
    store.add("0", e2).block();
    store.add("0", e3).block();
    StepVerifier.create(store.stream("0")).expectNext(e1, e2, e3).expectComplete().verify();
  }
}
