package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.stream.mem.MemStreamDB;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

class MemStreamDBTest {

  @Test
  void publish() {
    // Given
    var streamDB = new ArrayList<String>();
    var streamRepo = new MemStreamDB<>(streamDB);
    var msgs = createMsgs();
    // When
    var publish = streamRepo.publish(Flux.fromIterable(msgs));
    // Then
    //    StepVerifier.create(publish).expectNext(msgs.toArray(String[]::new)).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    var msgs = createMsgs();
    var streamRepo = new MemStreamDB<>(msgs);
    var expectedEvents = msgs.toArray(String[]::new);
    // When
    var subscribe = streamRepo.subscribe(0).map(Tuple2::_2);
    // Then
    StepVerifier.create(subscribe).expectNext(expectedEvents).verifyComplete();
  }

  private List<String> createMsgs() {
    return io.vavr.collection.List.range(0, 100).map(i -> "hello:" + i).toJavaList();
  }
}
