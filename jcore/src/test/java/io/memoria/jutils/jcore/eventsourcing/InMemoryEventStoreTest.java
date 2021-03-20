//package io.memoria.jutils.jcore.eventsourcing;
//
//import io.memoria.jutils.jcore.msgbus.MsgBusAdmin;
//import io.vavr.collection.List;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import reactor.test.StepVerifier;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class InMemoryEventStoreTest {
//  private static final String TRANS_ID = "0";
//  private final String topic = "firstTopic";
//  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> store;
//  private final MsgBusAdmin eventStore;
//
//  InMemoryEventStoreTest() {
//    store = new ConcurrentHashMap<>();
//    eventStore = new InMemoryEventStore(store);
//  }
//
//  @Test
//  @DisplayName("Events should be produced in the right order")
//  void order() {
//    // Given
//    int partition0 = 0;
//    var events0 = UserCreated.createMany(topic, 0);
//    var events1 = UserCreated.createMany(topic, 1);
//    // When
//    var actual0 = eventStore.publish(topic, partition0, TRANS_ID, events0).block();
//    var actual1 = eventStore.publish(topic, partition0, TRANS_ID, events1).block();
//    // Then
//    var expected = store.get(topic).get(partition0);
//    assert actual0 != null;
//    assertEquals(expected, actual0.appendAll(actual1));
//  }
//
//  @Test
//  @DisplayName("Published events should be same as inserted")
//  void samePublished() {
//    // Given
//    var events = List.range(0, 100).map(i -> (Event) new UserCreated(i, topic));
//    // When
//    var publishedEvents = eventStore.publish(topic, 0, TRANS_ID, events).block();
//    // Then
//    assertEquals(events, publishedEvents);
//  }
//
//  @Test
//  @DisplayName("Subscribed events should be same as in DB")
//  void sameSubscribed() {
//    // Given
//    int partition0 = 0;
//    var events = List.range(0, 100).map(i -> (Event) new UserCreated(i, topic));
//    store.put(topic, new ConcurrentHashMap<>());
//    store.get(topic).put(partition0, events);
//    // When
//    var publishedFlux = eventStore.subscribe(topic, partition0, 0);
//    // Then
//    StepVerifier.create(publishedFlux).expectNextCount(100).expectComplete().verify();
//  }
//
//  @Test
//  @DisplayName("Partitioning should work as expected")
//  void works() {
//    // Given
//    int partition0 = 0;
//    int partition1 = 1;
//    var events = List.range(0, 100).map(i -> new UserCreated(i, topic));
//    // When
//    var actual0 = eventStore.publish(topic, partition0, TRANS_ID, UserCreated.createMany(topic, 0)).block();
//    var actual1 = eventStore.publish(topic, partition1, TRANS_ID, UserCreated.createMany(topic, 1)).block();
//
//    // then
//    var expected0 = store.get(topic).get(partition0);
//    var expected1 = store.get(topic).get(partition1);
//    assert actual0 != null;
//    assert actual1 != null;
//    assertEquals(expected0, actual0);
//    assertEquals(expected1, actual1);
//  }
//}
