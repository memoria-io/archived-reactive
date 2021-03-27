package io.memoria.jutils.jcore.msgbus;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStore;
import io.memoria.jutils.jcore.eventsourcing.MemEventStore;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.usecase.user.UserEvent.UserCreated;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemEventStoreTest {
  private static final String TOPIC = "users_topic";
  private static final int PARTITION = 0;

  private final EventStore eventStore;
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> esDB;

  MemEventStoreTest() {
    this.esDB = new ConcurrentHashMap<>();
    this.eventStore = new MemEventStore(TOPIC, PARTITION, esDB);
  }

  @Test
  void publish() {
    // Given
    var batches = List.range(0, 100).map(i -> List.of((Event) new UserCreated(Id.of("eventId"), Id.of(i), "name" + i)));
    // When
    batches.map(eventStore::publish).map(Mono::block);
    // Then
    assertEquals(batches.flatMap(Function.identity()), esDB.get(TOPIC).get(PARTITION));
  }

  @Test
  void subscribe() {
    // Given
    var events = List.range(0, 100).map(i -> (Event) new UserCreated(Id.of("eventId"), Id.of(i), "name" + i));
    var expectedLastEvent = (Event) new UserCreated(Id.of("eventId"), Id.of(99), "name" + 99);
    // When
    esDB.put(TOPIC, new ConcurrentHashMap<>());
    esDB.get(TOPIC).put(PARTITION, events);
    // Then
    StepVerifier.create(eventStore.subscribe(0)).expectNext(events.toJavaArray(Event[]::new)).verifyComplete();
    StepVerifier.create(eventStore.last()).expectNext(expectedLastEvent);
  }
}
