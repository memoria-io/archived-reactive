package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.greeting.GreetingEvent;
import io.memoria.jutils.core.eventsourcing.stateless.EventStore;
import io.memoria.jutils.core.eventsourcing.stateless.InMemoryCommandHandler;
import io.memoria.jutils.core.value.Id;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
import static java.util.function.Function.identity;

class InMemoryCommandHandlerTest {
  private static final Id id = new Id("topic-" + new Random().nextInt(1000));
  private static final int MSG_COUNT = 10;
  private final EventStore eventStore;
  private final Map<Id, List<Event>> db;
  private final Flux<Event> events;
  private final Event[] expectedEvents;

  public InMemoryCommandHandlerTest() {
    this.db = new HashMap<>();
    this.eventStore = new InMemoryCommandHandler(db);
    // Given
    events = Flux.interval(ofMillis(1)).map(GreetingEvent::new).map(e -> (Event) e).take(MSG_COUNT);
    expectedEvents = Objects.requireNonNull(events.collectList().block()).toArray(new Event[0]);
  }

  @Test
  void addAndGet() {
    // When
    var addedEvents = events.concatMap(e -> eventStore.add(id, e));
    var eventsMono = eventStore.get(id);
    // Then
    StepVerifier.create(addedEvents).expectNext(expectedEvents).expectComplete().verify();
    StepVerifier.create(eventsMono).expectNext(Arrays.asList(expectedEvents)).expectComplete().verify();
  }

  @Test
  void addShouldBeInRightOrder() {
    // When
    var addedEvents = events.concatMap(e -> eventStore.add(id, e));
    // Then
    StepVerifier.create(addedEvents).expectNext(expectedEvents).expectComplete().verify();
  }

  @Test
  void transaction() throws InterruptedException {
    var es = Executors.newFixedThreadPool(10);
    var zero = new Id("0");
    var one = new Id("1");
    var two = new Id("2");
    int range = 20;
    int repeat = 10;
    for (int i = 0; i < repeat; i++) {
      es.submit(() -> {
        eventStore.transaction(zero, () -> add(zero, "zero_", range)).flatMap(identity()).subscribe();
        eventStore.transaction(one, () -> add(one, "one_", range)).flatMap(identity()).subscribe();
        eventStore.transaction(two, () -> add(two, "two_", range)).flatMap(identity()).subscribe();
      });
    }
    es.awaitTermination(1, TimeUnit.SECONDS);
    StepVerifier.create(eventStore.get(zero))
                .expectNext(expected(zero, "zero_", range, repeat))
                .expectComplete()
                .verify();
    StepVerifier.create(eventStore.get(one)).expectNext(expected(one, "one_", range, repeat)).expectComplete().verify();
    StepVerifier.create(eventStore.get(two)).expectNext(expected(two, "two_", range, repeat)).expectComplete().verify();
  }

  private Mono<Boolean> add(Id id, String newName, int range) {
    return Flux.range(0, range)
               .concatMap(x -> eventStore.add(id, new GreetingEvent(id, newName + x)))
               .collectList()
               .thenReturn(true);
  }

  private static List<Event> expected(Id id, String newName, int range, int repeat) {
    return Flux.range(0, range)
               .map(i -> (Event) new GreetingEvent(id, newName + i))
               .collectList()
               .repeat(repeat - 1)
               .flatMap(Flux::fromIterable)
               .collectList()
               .block();
  }
}
