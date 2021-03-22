package io.memoria.jutils.jcore.msgbus;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventStoreAdmin;
import io.memoria.jutils.jcore.eventsourcing.MemEventStoreAdmin;
import io.memoria.jutils.jcore.eventsourcing.data.user.UserEvent.UserCreated;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.Step;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    StepVerifier.create(admin.exists(topic,0)).expectNext(true).expectComplete().verify();
    StepVerifier.create(admin.exists(topic,1)).expectNext(true).expectComplete().verify();
    StepVerifier.create(admin.nOfPartitions(topic)).expectNext(2).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 0)).expectNext(1L).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 1)).expectNext(2L).verifyComplete();
  }
  
  @Test
  void increasePartitions(){
    // Given
    var topic = "topic";
    // When
    StepVerifier.create(admin.createTopic(topic, 2, 1)).verifyComplete();
    StepVerifier.create(admin.increasePartitionsTo(topic,5)).verifyComplete();
    // then
    StepVerifier.create(admin.nOfPartitions(topic)).expectNext(5).verifyComplete();
    assertEquals(5, store.get(topic).size());
  }
}
