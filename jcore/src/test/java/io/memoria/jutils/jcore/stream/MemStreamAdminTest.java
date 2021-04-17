package io.memoria.jutils.jcore.stream;

import io.memoria.jutils.jcore.stream.mem.MemStreamAdmin;
import io.vavr.collection.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemStreamAdminTest {
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> db;
  private final StreamAdmin admin;

  MemStreamAdminTest() {
    db = new ConcurrentHashMap<>();
    admin = new MemStreamAdmin(db);
  }

  @Test
  @DisplayName("streamAdmin should return valid metrics")
  void check() {
    // Given
    var topic = "topic";
    var msg0 = "hello world 0";
    var msg1 = "hello world 1";
    var stream0 = List.of(msg0);
    var stream1 = List.of(msg0, msg1);
    // When
    StepVerifier.create(admin.createTopic(topic, 2, 1)).verifyComplete();
    db.get(topic).put(0, stream0);
    db.get(topic).put(1, stream1);
    // Then
    StepVerifier.create(admin.exists(topic, 0)).expectNext(true).expectComplete().verify();
    StepVerifier.create(admin.exists(topic, 1)).expectNext(true).expectComplete().verify();
    StepVerifier.create(admin.nOfPartitions(topic)).expectNext(2).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 0)).expectNext(1L).verifyComplete();
    StepVerifier.create(admin.currentOffset(topic, 1)).expectNext(2L).verifyComplete();
  }

  @Test
  void increasePartitions() {
    // Given
    var topic = "topic";
    // When
    StepVerifier.create(admin.createTopic(topic, 2, 1)).verifyComplete();
    StepVerifier.create(admin.increasePartitionsTo(topic, 5)).verifyComplete();
    // then
    StepVerifier.create(admin.nOfPartitions(topic)).expectNext(5).verifyComplete();
    assertEquals(5, db.get(topic).size());
  }
}
