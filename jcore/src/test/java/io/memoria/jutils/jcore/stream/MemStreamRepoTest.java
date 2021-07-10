package io.memoria.jutils.jcore.stream;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.stream.mem.MemStream;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemStreamRepoTest {
  private static final String TOPIC = "users_topic";
  private static final int PARTITION = 0;

  private final StreamRepo streamRepo;
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Msg>>> esDB;

  MemStreamRepoTest() {
    this.esDB = new ConcurrentHashMap<>();
    this.streamRepo = new MemStream(TOPIC, PARTITION, esDB);
  }

  @Test
  void publish() {
    // Given
    var msgs = List.range(0, 100).map(i -> Msg.of(Id.of(i), "hello:" + i));
    // When
    msgs.map(streamRepo::publish).map(Mono::block);
    // Then
    assertEquals(msgs, esDB.get(TOPIC).get(PARTITION));
  }

  @Test
  void subscribe() {
    // Given
    var msgs = List.range(0, 100).map(i -> Msg.of(Id.of(i), "hello:" + i));
    var expectedEvents = msgs.toJavaArray(Msg[]::new);
    var expectedLastEvent = Msg.of(Id.of(99), "hello:99");
    // When
    esDB.put(TOPIC, new ConcurrentHashMap<>());
    esDB.get(TOPIC).put(PARTITION, msgs);
    // Then
    StepVerifier.create(streamRepo.subscribe(0)).expectNext(expectedEvents).verifyComplete();
    StepVerifier.create(streamRepo.last()).expectNext(expectedLastEvent);
  }

}
