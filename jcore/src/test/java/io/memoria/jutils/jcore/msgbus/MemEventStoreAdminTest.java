package io.memoria.jutils.jcore.msgbus;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStoreAdmin;
import io.memoria.jutils.jcore.eventsourcing.MemEventStoreAdmin;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserEvent.UserCreated;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;

class MemEventStoreAdminTest {
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> store;
  private final EventStoreAdmin admin;

  MemEventStoreAdminTest() {
    store = new ConcurrentHashMap<>();
    admin = new MemEventStoreAdmin(store);
  }

  @Test
  void check() {
    // Given
    var topic = "topic";
    // When
    StepVerifier.create(admin.createTopic(topic, 2, 1)).verifyComplete();
    store.get(topic).put(0, List.of(new UserCreated(Id.of("eventId"), Id.of(0), "name_0")));
    store.get(topic)
         .put(1,
              List.of(new UserCreated(Id.of("eventId"), Id.of(0), "name_0"),
                      new UserCreated(Id.of("eventId"), Id.of(1), "name_1")));
    // Then
    StepVerifier.create(admin.exists(topic)).expectNext(true).expectComplete().verify();
    StepVerifier.create(admin.nOfPartitions(topic)).expectNext(2).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 0)).expectNext(1L).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 1)).expectNext(2L).verifyComplete();
  }
}
