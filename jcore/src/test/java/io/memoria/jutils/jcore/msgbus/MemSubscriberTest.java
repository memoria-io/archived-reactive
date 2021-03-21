package io.memoria.jutils.jcore.msgbus;

import io.vavr.collection.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;

class MemSubscriberTest {
  private static final String TOPIC = "users_topic";
  private static final int PARTITION = 0;

  private final MsgBusSubscriber sub;
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> db;

  MemSubscriberTest() {
    this.db = new ConcurrentHashMap<>();
    this.sub = new MemSubscriber(TOPIC, PARTITION, 0, db);
  }

  @Test
  @DisplayName("Events should be produced in the right order, with right offset")
  void order() {
    // Given
    var msgs = List.range(0, 100).map(i -> "msg" + i);
    var map = new ConcurrentHashMap<Integer, List<String>>();
    map.put(PARTITION, msgs);
    db.put(TOPIC, map);
    // When
    var subs = sub.subscribe();
    // Then
    StepVerifier.create(subs).expectNext(msgs.toJavaArray(String[]::new)).verifyComplete();
  }
}
