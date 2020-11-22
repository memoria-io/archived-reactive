package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class InMemoryGreetingEventStoreTest {
  private static record GreetingCreated(Id id, String aggId, String value) implements Event {}

  private final Map<String, ArrayList<Event>> db = new HashMap<>();
  private final EventStore store = new InMemoryEventStore(db);
  private final GreetingCreated e1 = new GreetingCreated(new Id("0"), "0", "hello");
  private final GreetingCreated e2 = new GreetingCreated(new Id("1"), "1", "Bye");
  private final GreetingCreated e3 = new GreetingCreated(new Id("2"), "1", "Ciao");

  @Test
  void add() {
    var add1 = store.add("some_topic", Flux.just(e1));
    var add2 = store.add("another_topic", Flux.just(e2));
    StepVerifier.create(add1).expectNextCount(1).expectComplete().verify();
    StepVerifier.create(add2).expectNextCount(1).expectComplete().verify();
    Assertions.assertEquals(e1, db.get("some_topic").get(0));
    Assertions.assertEquals(e2, db.get("another_topic").get(0));
  }

  @Test
  void addMany() {
    var add = store.add("0", Flux.just(e1, e2, e3));
    StepVerifier.create(add).expectNextCount(3).expectComplete().verify();
    StepVerifier.create(store.stream("0")).expectNext(e1, e2, e3).expectComplete().verify();
  }

  @BeforeEach
  void beforeEach() {
    db.clear();
  }

  @Test
  void exists() {
    var add = store.add("1", Flux.just(e1));
    StepVerifier.create(add).expectNextCount(1).expectComplete().verify();
    Assertions.assertEquals(true, store.exists("1").block());
  }

  @Test
  void stream() {
    var add = store.add("0", Flux.just(e1, e2, e3));
    StepVerifier.create(add).expectNextCount(3).expectComplete().verify();
    StepVerifier.create(store.stream("0")).expectNext(e1, e2, e3).expectComplete().verify();
  }
}
