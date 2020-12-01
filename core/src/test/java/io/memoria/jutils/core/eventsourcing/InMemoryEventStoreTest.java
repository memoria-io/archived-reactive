package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.event.InMemoryEventStore;
import io.memoria.jutils.core.eventsourcing.greeting.GreetingEvent;
import io.memoria.jutils.core.value.Id;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMillis;

class InMemoryEventStoreTest {
  private static final Id id = new Id("topic-" + new Random().nextInt(1000));
  private static final int MSG_COUNT = 10;
  private final EventStore eventStore;
  private final Map<Id, List<Event>> db;
  private final Flux<Event> events;
  private final Event[] expectedEvents;

  public InMemoryEventStoreTest() {
    this.db = new HashMap<>();
    this.eventStore = new InMemoryEventStore(db);
    // Given
    events = Flux.interval(ofMillis(1)).map(GreetingEvent::new).map(e -> (Event) e).take(MSG_COUNT);
    expectedEvents = Objects.requireNonNull(events.collectList().block()).toArray(new Event[0]);
  }

  @Test
  void addShouldBeInRightOrder() {
    // When
    var addedEvents = events.concatMap(e -> eventStore.add(id, e));
    // Then
    StepVerifier.create(addedEvents).expectNext(expectedEvents).expectComplete().verify();
  }

  @Test
  void applyWithIdLockingTest() throws InterruptedException {
    var es = Executors.newFixedThreadPool(10);
    var idZero = new Id("0");
    var idOne = new Id("1");
    var idTwo = new Id("2");
    for (int i = 0; i < 3; i++) {
      es.submit(() -> {
        eventStore.apply(idZero, () -> loop(idZero)).subscribe();
        eventStore.apply(idOne, () -> loop(idOne)).subscribe();
        eventStore.apply(idTwo, () -> loop(idTwo)).subscribe();
      });
    }
    es.awaitTermination(1, TimeUnit.SECONDS);
    StepVerifier.create(eventStore.get(idZero)).expectNext(expected(idZero)).expectComplete().verify();
    StepVerifier.create(eventStore.get(idOne)).expectNext(expected(idOne)).expectComplete().verify();
    StepVerifier.create(eventStore.get(idTwo)).expectNext(expected(idTwo)).expectComplete().verify();
  }

  @Test
  void idLocking() throws InterruptedException {
    var es = Executors.newFixedThreadPool(10);
    var idZero = new Id("0");
    var idOne = new Id("1");
    var idTwo = new Id("2");
    for (int i = 0; i < 3; i++) {
      es.submit(() -> {
        transaction(idZero);
        transaction(idOne);
        transaction(idTwo);
      });
    }
    es.awaitTermination(1, TimeUnit.SECONDS);
    StepVerifier.create(eventStore.get(idZero)).expectNext(expected(idZero)).expectComplete().verify();
    StepVerifier.create(eventStore.get(idOne)).expectNext(expected(idOne)).expectComplete().verify();
    StepVerifier.create(eventStore.get(idTwo)).expectNext(expected(idTwo)).expectComplete().verify();
  }

  @Test
  void produceAndConsume() {
    // When
    var addedEvents = events.concatMap(e -> eventStore.add(id, e));
    var eventsMono = eventStore.get(id);
    // Then
    StepVerifier.create(addedEvents).expectNext(expectedEvents).expectComplete().verify();
    StepVerifier.create(eventsMono).expectNext(Arrays.asList(expectedEvents)).expectComplete().verify();
  }

  private List<Event> expected(Id id) {
    return Flux.range(0, 10)
               .map(i -> (Event) new GreetingEvent(id, "new_name_" + i))
               .collectList()
               .repeat(2)
               .flatMap(Flux::fromIterable)
               .collectList()
               .block();
  }

  private boolean loop(Id id) {
    for (int x = 0; x < 10; x++) {
      eventStore.add(id, new GreetingEvent(id, "new_name_" + x)).subscribe();
    }
    return true;
  }

  private void transaction(Id id) {
    eventStore.startTransaction(id);
    loop(id);
    eventStore.endTransaction(id);
  }
}
