package io.memoria.jutils.jcore.stream;

import io.memoria.jutils.jcore.stream.mem.MemStream;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemStreamRepoTest {
  private static final String TOPIC = "users_topic";
  private static final int PARTITION = 0;

  private final StreamRepo streamRepo;
  private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> esDB;

  MemStreamRepoTest() {
    this.esDB = new ConcurrentHashMap<>();
    this.streamRepo = new MemStream(TOPIC, PARTITION, esDB);
  }

  @Test
  void publish() {
    // Given
    var batches = List.range(0, 100).map(i -> "hello:" + i).map(List::of);
    // When

    batches.map(streamRepo::publish).map(Mono::block);
    // Then
    assertEquals(batches.flatMap(Function.identity()), esDB.get(TOPIC).get(PARTITION));
  }

  @Test
  void subscribe() {
    // Given
    var events = List.range(0, 100).map(i -> "hello:" + i);
    var expectedLastEvent = "hello:99";
    // When
    esDB.put(TOPIC, new ConcurrentHashMap<>());
    esDB.get(TOPIC).put(PARTITION, events);
    // Then
    StepVerifier.create(streamRepo.subscribe(0)).expectNext(events.toJavaArray(String[]::new)).verifyComplete();
    StepVerifier.create(streamRepo.last()).expectNext(expectedLastEvent);
  }

}
