//package io.memoria.jutils.jcore.msgbus;
//
//import io.vavr.collection.List;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import reactor.core.publisher.Flux;
//import reactor.test.StepVerifier;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class MemPublisherTest {
//  private static final String TOPIC = "users_topic";
//  private static final int PARTITION = 0;
//
//  private final MsgBusPublisher pub;
//  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> db;
//
//  MemPublisherTest() {
//    this.db = new ConcurrentHashMap<>();
//    this.pub = new MemPublisher(TOPIC, PARTITION, db);
//  }
//
//  @Test
//  @DisplayName("Events should be produced in the right order, with right offset")
//  void order() {
//    // Given
//    var msgs = Flux.range(0, 100).map(i -> "msg" + i);
//    // When
//    var pubs = msgs.concatMap(pub::publish);
//    // Then
//    StepVerifier.create(pubs).expectNext(List.range(1L, 101L).toJavaArray(Long[]::new)).verifyComplete();
//    assertEquals(List.range(0, 100).map(i -> "msg" + i), db.get(TOPIC).get(PARTITION));
//  }
//}
